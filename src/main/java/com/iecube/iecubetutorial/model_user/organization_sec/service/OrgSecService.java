package com.iecube.iecubetutorial.model_user.organization_sec.service;

import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrgSecService {

    OrgSec createOrgSec(OrgSec orgSec);

    OrgSec getById(Long id);

    List<OrgSec> getOrgSecByTop(Long oTopId);

    List<OrgSec> getOrgSecs();

    List<OrgSec> batchGet(List<Long> idList);
}
