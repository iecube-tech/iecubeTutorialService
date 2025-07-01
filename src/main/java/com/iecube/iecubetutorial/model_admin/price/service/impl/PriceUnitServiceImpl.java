package com.iecube.iecubetutorial.model_admin.price.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.JsonException;
import com.iecube.iecubetutorial.model_admin.approval.approval.dto.ApprovalDto;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approval.service.ApprovalService;
import com.iecube.iecubetutorial.model_admin.point.expire.service.impl.APointExpireServiceImpl;
import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import com.iecube.iecubetutorial.model_admin.price.mapper.PriceUnitMapper;
import com.iecube.iecubetutorial.model_admin.price.qo.PriceChangeQo;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PriceUnitServiceImpl implements PriceUnitService {

    @Autowired
    private PriceUnitMapper priceUnitMapper;


    @Override
    public List<PriceUnit> findAll() {
        return priceUnitMapper.getPriceUnits();
    }

    @Override
    public double GeneratePriceUnit() {
        return priceUnitMapper.GeneratePriceUnit();
    }

    @Override
    public double RechargePriceUnit() {
        return priceUnitMapper.RechargePriceUnit();
    }


}
