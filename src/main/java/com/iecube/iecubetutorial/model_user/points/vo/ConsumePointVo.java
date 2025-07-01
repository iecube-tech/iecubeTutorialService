package com.iecube.iecubetutorial.model_user.points.vo;

import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import lombok.Data;

import java.util.List;

@Data
public class ConsumePointVo {
    private double consumeTotal;
    private List<PointRecordVo> consumedRecord;
}
