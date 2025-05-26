package com.iecube.iecubetutorial.model.invitationCode.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model.email.servcie.EmailService;
import com.iecube.iecubetutorial.model.invitationCode.entity.InvitationCode;
import com.iecube.iecubetutorial.model.invitationCode.exception.CodeExistOrUsedException;
import com.iecube.iecubetutorial.model.invitationCode.exception.CodeLenException;
import com.iecube.iecubetutorial.model.invitationCode.exception.InvalidCodeException;
import com.iecube.iecubetutorial.model.invitationCode.mapper.InvitationCodeMapper;
import com.iecube.iecubetutorial.model.invitationCode.service.InvitationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class InvitationCodeServiceImpl implements InvitationCodeService {

    @Autowired
    private InvitationCodeMapper invitationCodeMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public InvitationCode createInvitationCode(String iCode) {
        if (iCode == null) {
            iCode = genCode();
        }else {
            if (iCode.length()!=6){
                throw new CodeLenException("邀请码长度不符合要求");
            }
            // todo校验传进来的邀请码
        }
        if(codeExist(iCode)){
            throw new CodeExistOrUsedException("生成的邀请码已存在");
        }
        InvitationCode invitationCode = new InvitationCode();
        invitationCode.setCode(iCode);
        invitationCode.setCreateTime(new Date());
        int res = invitationCodeMapper.addInvitationCode(invitationCode);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return invitationCode;
    }


    @Override
    public void bindCodeToEmail(InvitationCode invitationCode, String email) {
        if(emailExist(email)){
            throw new CodeExistOrUsedException("该邮箱已绑定邀请码");
        }
        invitationCode.setEmail(email);
        int res = invitationCodeMapper.updateInvitationCode(invitationCode);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        // todo 发送邮件
        String subject = "【曾益慧创】IECUBE Tutorial 邀请码";
        String msg = "【曾益慧创】您的 iecube tutorial 邀请码为："+invitationCode.getCode() +"，一个邮箱账户只可以申请一次邀请码，请妥善保存。祝您体验愉快。";
        emailService.sendSimpleMessage(email,subject,msg).exceptionally(ex->{
            log.error("发送邮件失败：{}",ex.getMessage());
            return null;
        });
    }

    @Override
    public void applyCode(String email) {
        if(emailExist(email)){
            throw new CodeExistOrUsedException("该邮箱已申请邀请码");
        }
        InvitationCode invitationCode = createInvitationCode(null);
        bindCodeToEmail(invitationCode, email);
    }

    @Override
    public InvitationCode applyCodeByEmail(String email){
        InvitationCode invitationCode = invitationCodeMapper.getInvitationCodeByEmail(email);
        if(invitationCode != null){
            return invitationCode;
        }
        invitationCode = createInvitationCode(null);
        invitationCode.setEmail(email);
        int res = invitationCodeMapper.updateInvitationCode(invitationCode);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return invitationCode;
    }

    @Override
    public boolean bindCodeToUser(String code, Long userId) {
        InvitationCode invitationCode = invitationCodeMapper.getInvitationCode(code);
        if(invitationCode == null){
            throw new InvalidCodeException("无效的邀请码");
        }
        if(invitationCode.getUserId() == null){
            invitationCode.setUserId(userId);
            int res =invitationCodeMapper.updateInvitationCode(invitationCode);
            if(res!=1){
                throw new UpdateException("更新数据异常");
            }
            return Boolean.TRUE;
        }
        if(!invitationCode.getUserId().equals(userId)){
            throw new CodeExistOrUsedException("邀请码已被绑定，非本账户邀请码");
        }
        return Boolean.TRUE;
    }

    private Boolean codeExist(String iCode) {
        InvitationCode invitationCode = invitationCodeMapper.getInvitationCode(iCode);
        return invitationCode!=null;
    }

    private Boolean emailExist(String email) {
        InvitationCode invitationCode = invitationCodeMapper.getInvitationCodeByEmail(email);
        return invitationCode!=null;
    }

    private String genCode(){
        // 生成 UUID
        UUID uuid = UUID.randomUUID();
        // 将 UUID 转换为字节数组
        byte[] uuidBytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (mostSigBits >> ((7 - i) * 8));
        }
        for (int i = 8; i < 16; i++) {
            uuidBytes[i] = (byte) (leastSigBits >> ((15 - i) * 8));
        }
        // 使用 Base64 编码并截取前6位
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
        return encoded.substring(0, 6);
    }
}
