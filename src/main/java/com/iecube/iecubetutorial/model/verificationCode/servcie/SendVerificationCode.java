package com.iecube.iecubetutorial.model.verificationCode.servcie;

public interface SendVerificationCode {

    void generateAndSendCode(String phoneNumber, String usage);

    boolean verifyCode(String phoneNumber, String usage, String code);
}
