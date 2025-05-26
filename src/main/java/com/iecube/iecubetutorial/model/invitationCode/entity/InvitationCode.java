package com.iecube.iecubetutorial.model.invitationCode.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InvitationCode {
    private Long id;
    private String code;
    private String email; // 一个邮箱只生成一个code
    private Long userId;  // 一个邀请码只可以给一个用户使用
    private Date createTime;
}
