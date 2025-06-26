package com.iecube.iecubetutorial.model_admin.approval.approvalLog.service;

import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity.ApprovalLog;

import java.util.List;

public interface ApprovalLogService {
    void recordLog(Approval approval, String operator, String action, String remark);
    List<ApprovalLog> getApprovalLogsByApprovalLogIds(List<Long> approvalLogIdList);
    List<ApprovalLog> getApprovalLogsByApprovalLogId(Long approvalLogId);

}
