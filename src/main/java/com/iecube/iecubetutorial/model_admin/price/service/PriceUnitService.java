package com.iecube.iecubetutorial.model_admin.price.service;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;

import java.util.List;

public interface PriceUnitService {

    List<PriceUnit> findAll();

    /**
     * @return **积分每生成
     */
    double GeneratePriceUnit();

    /**
     * @return **元每积分
     */
    double RechargePriceUnit();



}
