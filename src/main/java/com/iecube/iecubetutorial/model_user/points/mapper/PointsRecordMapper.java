package com.iecube.iecubetutorial.model_user.points.mapper;

import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointsRecordMapper {

    int create(PointsRecord record);

    List<PointsRecord> getByOSecId(Long oSecId);

    List<PointsRecord> getByAccount(Long accountId);
}
