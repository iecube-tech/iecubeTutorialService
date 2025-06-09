package com.iecube.iecubetutorial.model_admin.user.service.impl;

import com.iecube.iecubetutorial.model_admin.user.entity.AUserRole;
import com.iecube.iecubetutorial.model_admin.user.mapper.AUserRoleMapper;
import com.iecube.iecubetutorial.model_admin.user.service.AUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AUserRoleServiceImpl implements AUserRoleService {

    @Autowired
    private AUserRoleMapper aUserRoleMapper;

    @Override
    public List<AUserRole> roleList() {
        return aUserRoleMapper.allRoles();
    }
}
