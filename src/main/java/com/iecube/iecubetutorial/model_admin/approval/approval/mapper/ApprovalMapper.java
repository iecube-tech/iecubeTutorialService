package com.iecube.iecubetutorial.model_admin.approval.approval.mapper;

import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    int createApproval(Approval approval);

    Approval getApproval(long id);

    int updateApproval(Approval approval);

    List<Approval> getByApprover(String approver);

    List<Approval> getByCreator(String creator);
}
