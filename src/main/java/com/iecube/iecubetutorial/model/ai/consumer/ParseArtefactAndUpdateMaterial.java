package com.iecube.iecubetutorial.model.ai.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.ai.dto.ParseArtefactDto;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
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

    @Override
    public void run() {
        log.info("parse artefactId-->running");
        while(true){
            try{
                ParseArtefactDto parseArtefactDto = NewParseTask.take();
                handelParseArtefactDto(parseArtefactDto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void handelParseArtefactDto(ParseArtefactDto parseArtefactDto) {
        MaterialEntity material = materialService.getMaterial(parseArtefactDto.getMaterialId());
        if(!parseArtefactDto.getStatus().equals(MaterialStatus.FAILED.getStatus())){
            String content;
            try{
                JsonNode jsonNode = w6ApiService.getJsonRes(parseArtefactDto.getArtefactId());
                content = jsonNode.get("content").asText();
                material.setStatus(MaterialStatus.DONE.getStatus());
            }catch (Exception e){
                content = e.getMessage();
                material.setStatus(MaterialStatus.FAILED.getStatus());
            }
            material.setHtml(content);
            material.setUpdateTime(new Date());
        }
        else {
            material.setHtml(parseArtefactDto.getError());
            material.setUpdateTime(new Date());
        }
        materialService.handelUpload(material);
    }
}
