package com.iecube.iecubetutorial.model.mOutline.service.impl;

import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.mapper.MOutlineMapper;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MOutlineServiceImpl implements MOutlineService {

    @Autowired
    private MOutlineMapper mOutlineMapper;

    @Autowired
    private W6ApiService w6ApiService;

    @Override
    public MOutline genMOutline(MaterialQo materialQo) {
        MOutline mOutline = new MOutline();
        mOutline.setId(UUIDGenerator.generateUUID());
        mOutline.setName(materialQo.getName());
        mOutline.setTitle(materialQo.getTitle());
        mOutline.setKnowledgePoint(mOutline.getKnowledgePoint());
        mOutline.setCreateTime(Instant.now());
        return null;
    }
}
