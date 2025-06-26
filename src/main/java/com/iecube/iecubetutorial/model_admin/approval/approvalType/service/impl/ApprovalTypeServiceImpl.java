package com.iecube.iecubetutorial.model_admin.approval.approvalType.service.impl;

import com.iecube.iecubetutorial.model_admin.approval.approvalType.entity.ApprovalType;
import com.iecube.iecubetutorial.model_admin.approval.approvalType.mapper.ApprovalTypeMapper;
import com.iecube.iecubetutorial.model_admin.approval.approvalType.service.ApprovalTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalTypeServiceImpl implements ApprovalTypeService {

    @Autowired
    private ApprovalTypeMapper approvalTypeMapper;

    @Override
    public List<ApprovalType> getAll() {
        return approvalTypeMapper.getAll();
    }
}
