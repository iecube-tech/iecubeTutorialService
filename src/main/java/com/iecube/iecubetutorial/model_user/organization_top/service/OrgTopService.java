package com.iecube.iecubetutorial.model_user.organization_top.service;

import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;

import java.util.List;

public interface OrgTopService {

    void createOrgTop(OrgTop orgTop);

    OrgTop getById(Long id);

    List<OrgTop> getOrgTop();

    OrgTop updateOrgTop(OrgTop orgTop);

    List<OrgTop> deleteOrgTop(Long orgTopId);
}
