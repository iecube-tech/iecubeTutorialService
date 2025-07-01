package com.iecube.iecubetutorial.model_user.points.mapper;

import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import com.iecube.iecubetutorial.model_user.points.vo.PointRecordVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointsRecordMapper {

    int create(PointsRecord record);

    List<PointsRecord> getByOSecId(Long oSecId);

    List<PointsRecord> getByAccount(Long accountId);

    List<PointRecordVo> oSecConsume(Long oSecId);
    List<PointRecordVo> oSecRecharge(Long oSecId);
}
