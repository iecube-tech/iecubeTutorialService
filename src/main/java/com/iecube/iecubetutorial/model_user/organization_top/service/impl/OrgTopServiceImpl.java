package com.iecube.iecubetutorial.model_user.organization_top.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import com.iecube.iecubetutorial.model_user.organization_top.mapper.OrgTopMapper;
import com.iecube.iecubetutorial.model_user.organization_top.service.OrgTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrgTopServiceImpl implements OrgTopService {

    @Autowired
    private OrgTopMapper orgTopMapper;


    @Override
    public void createOrgTop(OrgTop orgTop) {
        int res = orgTopMapper.createOrgTop(orgTop);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public OrgTop getById(Long id) {
        return orgTopMapper.getById(id);
    }

    @Override
    public List<OrgTop> getOrgTop() {
        return orgTopMapper.getNotRemoved();
    }

    @Override
    public OrgTop updateOrgTop(OrgTop orgTop) {
        orgTop.setLastOperator(ThreadLocalUtil.getPhone());
        orgTop.setLastOperateTime(Instant.now());
        int res = orgTopMapper.updateOrgTop(orgTop);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return orgTopMapper.getById(orgTop.getId());
    }

    @Override
    public List<OrgTop> deleteOrgTop(Long orgTopId) {
        int res = orgTopMapper.deleteOrgTop(orgTopId);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return this.getOrgTop();
    }


}
