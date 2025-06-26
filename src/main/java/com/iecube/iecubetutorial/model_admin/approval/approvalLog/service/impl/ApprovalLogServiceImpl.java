package com.iecube.iecubetutorial.model_admin.approval.approvalLog.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity.ApprovalLog;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.mapper.ApprovalLogMapper;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.service.ApprovalLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ApprovalLogServiceImpl implements ApprovalLogService {

    @Autowired
    private ApprovalLogMapper approvalLogMapper;

    @Override
    public void recordLog(Approval approval, String operator, String action, String remark) {
        ApprovalLog log = new ApprovalLog();
        log.setApprovalId(approval.getId());
        log.setAction(action);
        log.setRemark(remark);
        log.setCreator(operator);
        log.setLastOperator(operator);
        log.setRemoved(0);
        log.setCreateTime(Instant.now());
        log.setLastOperateTime(Instant.now());
        int res = approvalLogMapper.addLog(log);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public List<ApprovalLog> getApprovalLogsByApprovalLogIds(List<Long> ApprovalIdList) {
        return approvalLogMapper.getApprovalLogByApprovalIds(ApprovalIdList);
    }

    @Override
    public List<ApprovalLog> getApprovalLogsByApprovalLogId(Long approvalLogId) {
        return approvalLogMapper.getApprovalLogByApprovalId(approvalLogId);
    }
}
