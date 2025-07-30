package com.iecube.iecubetutorial.model_admin.price.service;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;

import java.util.List;

public interface PriceUnitService {

    List<PriceUnit> findAll();

    /**
     * @return ** __token每积分
     */
    double targetTokenPerPoint();

    /**
     * @return **__积分每元
     */
    double targetPointsPerRMB();

}
