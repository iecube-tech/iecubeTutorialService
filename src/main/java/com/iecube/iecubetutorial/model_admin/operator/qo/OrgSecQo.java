package com.iecube.iecubetutorial.model_admin.operator.qo;

import lombok.Data;

import java.util.List;

@Data
public class OrgSecQo {
    private String approver;
    private Long orgTop;
    private String name;
    private String type;
    private int limit;
    private double giftPoints;
    private double reWriteGiftPoints;
    private List<UUserQo> uUserQoList;
}
