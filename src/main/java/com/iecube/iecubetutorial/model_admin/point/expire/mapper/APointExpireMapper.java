package com.iecube.iecubetutorial.model_admin.point.expire.mapper;

import com.iecube.iecubetutorial.model_admin.point.expire.entity.ExpireDays;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface APointExpireMapper {
    int newExpireDays(ExpireDays expireDays);
    ExpireDays getExpireDays();
    int removeExpireDays(ExpireDays expireDays);
}
