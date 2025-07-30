package com.iecube.iecubetutorial.model_admin.price.mapper;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PriceUnitMapper {
    List<PriceUnit> getPriceUnits();

    int UpdatePriceUnit(PriceUnit priceUnit);

    /**
     * @return **Token每积分
     */
    double targetTokenPerPoint();

    /**
     * @return ** __积分每元
     */
    double targetPointsPerRMB();

    void disableAll();

    int createPrice(PriceUnit priceUnit);
}
