package com.iecube.iecubetutorial.model_admin.price.service.impl;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import com.iecube.iecubetutorial.model_admin.price.mapper.PriceUnitMapper;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
