package com.iecube.iecubetutorial.model.user.dto;

import lombok.Data;

@Data
public class LoginQo {
    private String phone;
    private String invitationCode;
    private String verifyCode;
}
