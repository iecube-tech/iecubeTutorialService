package com.iecube.iecubetutorial.model.sms.service;

public interface SmsService {
    boolean sendRegisterSms(String phoneNumber, String message);
    boolean sendLoginSms(String phoneNumber, String message);
}