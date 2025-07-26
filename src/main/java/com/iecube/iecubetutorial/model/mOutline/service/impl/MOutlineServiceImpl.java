package com.iecube.iecubetutorial.model.mOutline.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.mapper.MOutlineMapper;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class MOutlineServiceImpl implements MOutlineService {

    @Autowired
    private MOutlineMapper mOutlineMapper;

    @Autowired
    private W6ApiService w6ApiService;

    @Override
    public MOutline genMOutline(MaterialQo materialQo, boolean isOneClick, String projectId) {
        MOutline mOutline = new MOutline();
        mOutline.setId(UUIDGenerator.generateUUID());
        mOutline.setName(materialQo.getName());
        mOutline.setTitle(materialQo.getTitle());
        mOutline.setKnowledgePoint(materialQo.getKnowledgePoints());
        mOutline.setChatId(w6ApiService.genChat());
        mOutline.setCreateTime(Instant.now());
        mOutline.setCreator(ThreadLocalUtil.getAccountId());
        mOutline.setShow(!isOneClick);
        int res = mOutlineMapper.insertOutline(mOutline);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return mOutline;
    }

    @Override
    public MOutline getById(String id) {
        return mOutlineMapper.getOutlineById(id);
    }

    @Override
    public MOutline getByChatId(String chatId) {
        return mOutlineMapper.getOutlineByChatId(chatId);
    }

    @Override
    public MOutline updateMOutline(MOutline mOutline) {
        MOutline oldOutline = mOutlineMapper.getOutlineById(mOutline.getId());
        if(oldOutline==null){
            throw new UpdateException("未找到内容");
        }
        oldOutline.setProjectId(mOutline.getProjectId());
        oldOutline.setOutline(mOutline.getOutline());
        oldOutline.setSentToken(mOutline.getSentToken());
        oldOutline.setRecvToken(mOutline.getRecvToken());
        int res = mOutlineMapper.updateOutline(oldOutline);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return oldOutline;
    }

    @Override
    public MOutline getByProjectId(String projectId) {
        return mOutlineMapper.getOutlineByPId(projectId);
    }
}
