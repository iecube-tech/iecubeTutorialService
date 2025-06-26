package com.iecube.iecubetutorial.model_admin.approval.approvalType.mapper;

import com.iecube.iecubetutorial.model_admin.approval.approvalType.entity.ApprovalType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalTypeMapper {
    List<ApprovalType> getAll();
}
