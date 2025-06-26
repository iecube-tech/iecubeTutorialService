package com.iecube.iecubetutorial.model_user.organization_sec.mapper;

import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgSecMapper {
    int createOrgSec(OrgSec orgSec);
    OrgSec getById(Long id);
    List<OrgSec> getOrgSecByTop(Long oTopId);
    List<OrgSec> getNotRemoved();
    List<OrgSec> batchGet(@Param("idList") List<Long> idList);
}
