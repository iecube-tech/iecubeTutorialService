package com.iecube.iecubetutorial.model_admin.point.expire.service.impl;

import com.iecube.iecubetutorial.model_admin.point.expire.entity.ExpireDays;
import com.iecube.iecubetutorial.model_admin.point.expire.mapper.APointExpireMapper;
import com.iecube.iecubetutorial.model_admin.point.expire.service.APointExpireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class APointExpireServiceImpl implements APointExpireService {

    @Autowired
    private APointExpireMapper pointExpireMapper;

    @Override
    public int getExpireDays() {
        ExpireDays expireDays = pointExpireMapper.getExpireDays();
        return expireDays.getDays();
    }
}
