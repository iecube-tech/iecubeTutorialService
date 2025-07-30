package com.iecube.iecubetutorial.model_user.points.vo;

import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import lombok.Data;

@Data
public class PointRecordVo extends PointsRecord {
    private String secName;
    private String topName;
    private String userName;
    private String phone;
    private String projectName;
    private String projectTitle;
    private String projectKnowledgePoint;
    private String type;
}
