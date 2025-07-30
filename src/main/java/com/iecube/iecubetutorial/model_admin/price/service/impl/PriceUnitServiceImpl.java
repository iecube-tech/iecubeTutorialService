package com.iecube.iecubetutorial.model_admin.price.service.impl;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import com.iecube.iecubetutorial.model_admin.price.mapper.PriceUnitMapper;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
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
    public double targetTokenPerPoint() {
        return priceUnitMapper.targetTokenPerPoint();
    }

    @Override
    public double targetPointsPerRMB() {
        return priceUnitMapper.targetPointsPerRMB();
    }


}
