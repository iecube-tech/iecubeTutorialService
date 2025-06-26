package com.iecube.iecubetutorial.model_admin.price.mapper;

import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PriceUnitMapper {
    List<PriceUnit> getPriceUnits();

    int UpdatePriceUnit(PriceUnit priceUnit);

    /**
     * @return **积分每生成
     */
    double GeneratePriceUnit();

    /**
     * @return **元每积分
     */
    double RechargePriceUnit();
}
