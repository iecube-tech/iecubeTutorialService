package com.iecube.iecubetutorial.model_admin.approval.approval.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity.ApprovalLog;
import lombok.Data;

import java.util.List;

@Data
public class Approval extends BaseEntity {
    private Long id;
    private String approvalType;
    private String approverPhone; // 审批人phone
    private String status; // 状态：PENDING, APPROVED, REJECTED
    private String reason; // 审批原因/备注
    private String content; // 审批内容JSON
    private List<ApprovalLog> approvalLogs;
}
