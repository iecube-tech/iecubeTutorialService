package com.iecube.iecubetutorial.model.user.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.email.servcie.EmailService;
import com.iecube.iecubetutorial.model.invitationCode.entity.InvitationCode;
import com.iecube.iecubetutorial.model.invitationCode.exception.InvalidCodeException;
import com.iecube.iecubetutorial.model.invitationCode.mapper.InvitationCodeMapper;
import com.iecube.iecubetutorial.model.invitationCode.service.InvitationCodeService;
import com.iecube.iecubetutorial.model.resource.exception.HandelFileFailedException;
import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.dto.RegisterQo;
import com.iecube.iecubetutorial.model.user.dto.UserImportQo;
import com.iecube.iecubetutorial.model.user.dto.UserImportRes;
import com.iecube.iecubetutorial.model.user.entity.User;
import com.iecube.iecubetutorial.model.user.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.model.user.mapper.UserMapper;
import com.iecube.iecubetutorial.model.user.service.UserService;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import com.iecube.iecubetutorial.model.user.vo.UserImportVo;
import com.iecube.iecubetutorial.model.verificationCode.servcie.SendVerificationCode;
import com.iecube.iecubetutorial.util.jwt.AuthUtils;
import com.iecube.iecubetutorial.util.jwt.CurrentUser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SendVerificationCode verificationCodeService;

    @Autowired
    private InvitationCodeService invitationCodeService;

    @Autowired
    private InvitationCodeMapper invitationCodeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    /**
     * 手机号是否已经注册
     * @param phone 手机号
     * @return boolean
     */
    @Override
    public boolean phoneExists(String phone) {
        User user = userMapper.getUserByPhone(phone);
        return user != null;
    }

    @Override
    public void register(RegisterQo registerQo) {
        if(phoneExists(registerQo.getPhone())) {
            throw new PhoneUnavailableException("手机号已注册");
        }
        boolean verify = verificationCodeService.verifyCode(registerQo.getPhone(),"register",registerQo.getCode());
        if(!verify){
           throw new InvalidCodeException("验证码校验未通过");
        }
        User user = new User();
        user.setPhone(registerQo.getPhone());
        user.setName(registerQo.getName());
        user.setSchool(registerQo.getSchool());
        user.setCollage(registerQo.getCollage());
        user.setRole("user");
        user.setCreateTime(new Date());
        int res = userMapper.addUser(user);
        if(res != 1){
            throw new InsertException("服务错误，新增数据异常");
        }
    }

    @Override
    public LoginVo jwtLogin(LoginQo loginQo, StringRedisTemplate stringRedisTemplate) {
        User user = userMapper.getUserByPhone(loginQo.getPhone());
        if(user == null){
            throw new PhoneUnavailableException("用户尚未注册，请先注册");
        }
        boolean verify = verificationCodeService.verifyCode(loginQo.getPhone(),"login", loginQo.getVerifyCode());
        if(!verify){
            throw new InvalidCodeException("验证码校验未通过");
        }
        boolean invitation = invitationCodeService.bindCodeToUser(loginQo.getInvitationCode(), user.getId());
        if(!invitation){
            throw new InvalidCodeException("邀请码校验未通过");
        }
        LoginVo loginVo = new LoginVo();
        loginVo.setUser(user);
        String token = new AuthUtils().createToken(user.getId(),user.getPhone());
        loginVo.setToken(token);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(user.getId());
        currentUser.setPhone(user.getPhone());
        AuthUtils.cache(currentUser,token, stringRedisTemplate);
        return loginVo;
    }

    @Override
    public UserImportVo processExcel(MultipartFile file){
        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<UserImportQo> importList = new ArrayList<>();
            // 从第2行开始解析数据（跳过表头）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                UserImportQo importQo = new UserImportQo();
                // D列(索引3)到I列(索引8)
                importQo.setSchool(getCellValueAsString(row.getCell(0)));
                importQo.setCollage(getCellValueAsString(row.getCell(1)));
                importQo.setName(getCellValueAsString(row.getCell(2)));
                importQo.setPhone(getCellValueAsString(row.getCell(3)));
                importQo.setEmail(getCellValueAsString(row.getCell(4)));
                importList.add(importQo);
            }
            List<UserImportQo> filedList = new ArrayList<>();
            List<UserImportQo> successList = new ArrayList<>();
            importList.forEach(userImportQo -> {
                UserImportRes result = saveUserAndSendInvCode(userImportQo);
                userImportQo.setRes(result);
                if(result.isSuccess()){
                    successList.add(userImportQo);
                }else {
                    filedList.add(userImportQo);
                }
            });
            UserImportVo userImportVo = new UserImportVo();
            userImportVo.setFiledList(filedList);
            userImportVo.setSuccessList(successList);
            return userImportVo;
        }catch (IOException e){
            throw new HandelFileFailedException("服务错误，处理文件异常");
        }
    }

    private UserImportRes saveUserAndSendInvCode(UserImportQo importQo){
        UserImportRes result = new UserImportRes();
        User user = new User();
        user.setRole("user");
        user.setName(importQo.getName());
        user.setSchool(importQo.getSchool());
        user.setCollage(importQo.getCollage());
        user.setPhone(importQo.getPhone());
        user.setCreateTime(new Date());
        if(phoneExists(user.getPhone())){
            result.setSuccess(false);
            result.setReason(String.format("手机号：%s 已存在",user.getPhone()));
            return result;
        }
        int res = userMapper.addUser(user);
        if(res != 1){
            result.setSuccess(false);
            result.setReason("创建用户失败");
            return result;
        }
        InvitationCode invitationCode = invitationCodeService.applyCodeByEmail(importQo.getEmail());
        if(invitationCode.getUserId()!=null){
            result.setSuccess(false);
            result.setReason("邮箱已申请过邀请码且已绑定用户");
            return result;
        }
        try {
            invitationCodeService.bindCodeToUser(invitationCode.getCode(), user.getId());
        }catch (Exception e){
            result.setSuccess(false);
            result.setReason("邀请码绑定到用户失败");
            return result;
        }
        try{
            String subject = "【曾益慧创】IECUBE Tutorial 内测邀请";
            String text =String.format(
                    """
                    尊敬的%s老师:
                        您好!诚挚感谢您参与IECUBE Tutorial数字化动态讲义生成系统的内测计划!您的专业见解将为我们产品的优化提供重要支持。以下是内测专属信息及重要提示，请仔细查阅：
                        【内测登录信息】
                        内测平台：https://tutorial.iecube.online/
                        手机号：%s
                        内测码：%s
                        【注意事项】
                        内测时段：2025年5月26日12:00-6月10日17:00
                        权限说明：内测生成讲义不限制页面数，内测有效期内无限量生成
                        保密协议：测试内容及界面设计暂不对外公开传播
                        【服务支持人员】
                        为了给您提供最好的内测服务，我司为您分配了专属服务支持人员史宪羽。烦请填加史老师的微信(13691016878)，在内测过程中有什么问题或建议，可以随时联系我方人员，进行解决
                        再次感谢您对我们公司的支持!期待您的内测反馈，谢谢!
                    """,user.getName(),user.getPhone(), invitationCode.getCode());
            emailService.sendSimpleMessage(importQo.getEmail(), subject,text);
        }catch (Exception e){
            result.setSuccess(false);
            result.setReason("发送邮件失败");
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString().trim();
                } else {
                    yield String.valueOf((long) cell.getNumericCellValue()).trim();
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
