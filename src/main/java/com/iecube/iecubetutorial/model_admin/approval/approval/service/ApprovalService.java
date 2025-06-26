package com.iecube.iecubetutorial.model_admin.approval.approval.service;

import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;

import java.util.List;

public interface ApprovalService {

    List<Approval> getByApprover();

    List<Approval> getByCreator();

    Approval createApproval(Approval approval);

    Approval approval(Long id, String remark);

    Approval reject(Long id, String remark);

    void executeApprovedAction(Approval approval);
}
