package com.iecube.iecubetutorial.model_admin.approval.approvalLog.mapper;

import com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity.ApprovalLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalLogMapper {
    int addLog(ApprovalLog log);

    List<ApprovalLog> getApprovalLogByApprovalIds(@Param("approvalIdList") List<Long> approvalIdList);

    List<ApprovalLog> getApprovalLogByApprovalId(Long approvalId);
}
