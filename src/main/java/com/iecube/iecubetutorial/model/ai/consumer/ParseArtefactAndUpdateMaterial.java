package com.iecube.iecubetutorial.model.ai.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.ai.dto.ParseArtefactDto;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class ParseArtefactAndUpdateMaterial implements Runnable {

    private final BlockingQueue<ParseArtefactDto> NewParseTask;

    public ParseArtefactAndUpdateMaterial(BlockingQueue<ParseArtefactDto> NewParseTask ) {
        this.NewParseTask = NewParseTask;
        log.info("parse artefactId consumer-->start");
    }

    @Autowired
    private W6ApiService w6ApiService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PointsService pointsService;

    @Override
    public void run() {
        log.info("parse artefactId-->running");
        while(true){
            try{
                ParseArtefactDto parseArtefactDto = NewParseTask.take();
                log.info("parse artefactId 任务：{}",parseArtefactDto);
                handelParseArtefactDto(parseArtefactDto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void handelParseArtefactDto(ParseArtefactDto parseArtefactDto) {
        MaterialEntity material = materialService.getMaterial(parseArtefactDto.getMaterialId());
        if(material == null){
            log.warn("根据{}获取的material为null， 任务失败",parseArtefactDto.getMaterialId());
            return;
        }
        log.info("目标material: id:{}, 账户：{},{},{} ", material.getId(),material.getUserId(), material.getName(),material.getTitle());
        log.info("parse artefactId 任务: parseArtefactDto.getStatus():{}",parseArtefactDto.getStatus());
        if(!parseArtefactDto.getStatus().equals(MaterialStatus.FAILED.getStatus())){
            String content;
            try{
                JsonNode jsonNode = w6ApiService.getJsonRes(parseArtefactDto.getArtefactId());
                content = jsonNode.get("content").asText();
                material.setStatus(MaterialStatus.DONE.getStatus());
                Account account = accountService.getAccount(material.getUserId());
                pointsService.consumePoints(account, material);
            }catch (Exception e){
                content = e.getMessage();
                material.setStatus(MaterialStatus.FAILED.getStatus());
            }
            material.setHtml(content);
            material.setUpdateTime(new Date());
        }
        else {
            log.info("parse artefactId 任务: parseArtefactDto.getStatus():{}",parseArtefactDto.getStatus());
            material.setStatus(MaterialStatus.FAILED.getStatus());
            material.setHtml(parseArtefactDto.getError());
            material.setUpdateTime(new Date());
        }
        materialService.handelUpload(material);
    }
}
