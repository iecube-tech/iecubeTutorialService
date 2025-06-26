package com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class ApprovalLog extends BaseEntity {
    private Long id;
    private Long approvalId;
    private String action; // 操作：SUBMIT, APPROVE, REJECT
    private String remark; // 备注
}
