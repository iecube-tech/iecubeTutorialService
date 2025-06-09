package com.iecube.iecubetutorial.model_admin.user.mapper;

import com.iecube.iecubetutorial.model_admin.user.entity.AUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AUserRoleMapper {

    List<AUserRole> allRoles();
}
