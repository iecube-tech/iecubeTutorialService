package com.iecube.iecubetutorial.model_user.organization_top.mapper;

import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrgTopMapper {
    int createOrgTop(OrgTop orgTop);

    List<OrgTop> getNotRemoved();

    OrgTop getById(Long id);

    int updateOrgTop(OrgTop orgTop);

    int deleteOrgTop(Long orgTopId);
}
