package com.iecube.iecubetutorial.model.user.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.PasswordNotMatchException;
import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.entity.User;
import com.iecube.iecubetutorial.model.user.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.model.user.mapper.UserMapper;
import com.iecube.iecubetutorial.model.user.service.UserService;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import com.iecube.iecubetutorial.util.jwt.AuthUtils;
import com.iecube.iecubetutorial.util.jwt.CurrentUser;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.iecube.iecubetutorial.util.SHA256;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginVo jwtLogin(LoginQo loginQo, StringRedisTemplate stringRedisTemplate) {
        User user = userMapper.getUserByAccount(loginQo.getAccount());
        if(user == null){
            throw new PhoneUnavailableException("用户不存在");
        }
        // 检测密码
        // 先获取数据库加密后的密码 盐值  和用户传递过来的密码(相同的方法进行加密)进行比较
        String salt = user.getSalt();
        String oldMd5Password = user.getPassword();
        String newMd5Password = getMD5Password(loginQo.getPassword(), salt);
        if (!newMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("用户密码错误");
        }
        LoginVo loginVo = new LoginVo();
        loginVo.setUser(user);
        String token = new AuthUtils().createToken(user.getId(),user.getAccount());
        loginVo.setToken(token);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(user.getId());
        currentUser.setAccount(user.getAccount());
        AuthUtils.cache(currentUser,token, stringRedisTemplate);
        return loginVo;
    }

    public User addUser(String account, String password, String name){
        // todo 校验账户名是不是已经存在
        User existUser = userMapper.getUserByAccount(account);
        if(existUser != null){
            throw new PhoneUnavailableException("账号已存在，请更换账号");
        }
        User user = new User();
        String salt = UUID.randomUUID().toString().toUpperCase();
        // sha256先加密 再使用md5 对sha256加密
        String sha256Password = SHA256.encryptStringWithSHA256(password);
        String md5Password = getMD5Password(sha256Password, salt);
        user.setAccount(account);
        user.setPassword(md5Password);
        user.setName(name);
        user.setSalt(salt);
        int res = userMapper.addUser(user);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        user.setPassword(password);
        return user;
    }

    /**定义一个md5算法加密**/
    private static String getMD5Password(String password, String salt){
        // md5加密算法的方法 进行3次
        for (int i=0; i<3; i++){
            password = DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
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
