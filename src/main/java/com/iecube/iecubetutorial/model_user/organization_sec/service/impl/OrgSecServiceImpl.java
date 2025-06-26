package com.iecube.iecubetutorial.model_user.organization_sec.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_sec.mapper.OrgSecMapper;
import com.iecube.iecubetutorial.model_user.organization_sec.service.OrgSecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgSecServiceImpl implements OrgSecService {

    @Autowired
    private OrgSecMapper orgSecMapper;

    @Override
    public OrgSec createOrgSec(OrgSec orgSec) {
        int res = orgSecMapper.createOrgSec(orgSec);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return orgSec;
    }

    @Override
    public OrgSec getById(Long id) {
        return orgSecMapper.getById(id);
    }

    @Override
    public List<OrgSec> getOrgSecByTop(Long oTopId) {
        return orgSecMapper.getOrgSecByTop(oTopId);
    }

    @Override
    public List<OrgSec> getOrgSecs() {
        return orgSecMapper.getNotRemoved();
    }

    @Override
    public List<OrgSec> batchGet(List<Long> idList) {
        return orgSecMapper.batchGet(idList);
    }
}
