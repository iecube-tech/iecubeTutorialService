package com.iecube.iecubetutorial.model_admin.point.expire.service;


public interface APointExpireService {

    int getExpireDays();

    void changeExpireDays(int days, String creator, String approver);
}
