package com.iecube.iecubetutorial.model.materials.service.impl;

import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.exception.FailedToCreateTaskException;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialChatMapper;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialMapper;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.qo.UpMaterialQo;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.model.materials.vo.MaterialVo;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.mapper.ResourceMapper;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.model.user.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MaterialChatMapper materialChatMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private W6ApiService w6ApiService;

    @Autowired
    private ResourceService resourceService;

    private final BlockingQueue<MaterialChat> NewConnectTask;

    public MaterialServiceImpl(BlockingQueue<MaterialChat> NewConnectTask){
        this.NewConnectTask=NewConnectTask;
    }


    @Override
    public void generateMaterial(MaterialQo materialQo, Long userId) {
        MaterialEntity material = new MaterialEntity(); // material
        material.setUserId(userId);
        material.setName(materialQo.getName());
        material.setTitle(materialQo.getTitle());
        material.setKnowledgePoint(materialQo.getKnowledgePoints());
        material.setInstruction(materialQo.getInstruction());
        material.setStatus(MaterialStatus.NOTReady.getStatus());
        material.setDeleted(0);
        material.setCreateTime(new Date());
        int res = materialMapper.addMaterial(material);
        if(res!=1){
            throw new InsertException("服务错误，新增数据异常");
        }
        String chatId = w6ApiService.genChat();
        MaterialChat materialChat = new MaterialChat();  // materialChat
        materialChat.setChatId(chatId);
        materialChat.setMaterialId(material.getId());
        int res2 = materialChatMapper.addMaterialChat(materialChat);
        if(res2!=1){
            throw new InsertException("服务错误，新增数据异常");
        }
        // todo 和 生产消费者模型 w6建立websocket连接，处理生成任务  连接之后 material.setStatus(MaterialStatus.GENERATING.getStatus()); 更新状态
        try {
            NewConnectTask.put(materialChat);
            log.info("创建W6任务：{}",chatId);
        } catch (InterruptedException e) {
            throw new FailedToCreateTaskException(e.getMessage());
        }

        // 调用ai
        w6ApiService.usePageMaker(chatId, materialQo.getTitle(), materialQo.getKnowledgePoints(), materialQo.getInstruction());
    }

    @Override
    public List<MaterialVo> getMaterials(Long userId) {
        List<MaterialEntity> entities = materialMapper.getMaterials(userId);
        List<MaterialVo> vos = new ArrayList<>();
        entities.forEach(entity -> {
            MaterialVo vo = entityToVo(entity);
            vo.setHtml(null); // todo 暂时不需要返回html文本 不传输
            vo.getResource().setId(entity.getResource());
            vos.add(vo);
        });
        vos.forEach(vo->{
            if(vo.getResource().getId()!=null){
                Resource resource = resourceMapper.getResource(vo.getResource().getId());
                vo.setResource(resource==null?new Resource():resource);
            }
        });
        return vos;
    }

    @Override
    public MaterialEntity getMaterial(String chatId) {
        return materialMapper.getMaterialByChatId(chatId);
    }

    @Override
    public MaterialEntity getMaterial(Long id) {
        return materialMapper.getMaterial(id);
    }

    @Override
    public void handelUpload(MaterialEntity materialEntity) {
//        log.error("materialEntity status:{}", materialEntity.getStatus());
        if(materialEntity.getStatus().equals(MaterialStatus.DONE.getStatus())){
//            log.error("ohaaaaaaaaahhhhahahahah");
            Resource resource = resourceService.writeHtmlToFile(materialEntity.getHtml());
            Resource nRe =  resourceService.saveResource(resource);
            materialEntity.setResource(nRe.getId());
        }
        materialMapper.updateMaterial(materialEntity);
    }

    @Override
    public MaterialVo updateMaterial(UpMaterialQo upMaterialQo, Long userId) {
        if(upMaterialQo.getId()==null){
            throw new AuthException("没有权限");
        }
        MaterialEntity materialEntity = materialMapper.getMaterial(upMaterialQo.getId());
        if(!userId.equals(materialEntity.getUserId())){
            throw new AuthException("没有权限");
        }
        Resource resource = resourceMapper.getResource(materialEntity.getResource());
        Resource nResource = resourceService.updateResource(upMaterialQo.getHtmlContentBase64(), resource);
        materialEntity.setUpdateTime(new Date());
        int res = materialMapper.updateMaterial(materialEntity);
        if(res!=1){
            throw new UpdateException("服务错误, 更新数据异常");
        }
        MaterialVo materialVo = entityToVo(materialEntity);
        materialVo.setResource(nResource);
        return materialVo;
    }

    @Override
    public List<MaterialVo> deleteMaterial(Long id, Long userId) {
        if(id==null){
            throw new AuthException("没有权限");
        }
        MaterialEntity materialEntity = materialMapper.getMaterial(id);
        if(!userId.equals(materialEntity.getUserId())){
            throw new AuthException("没有权限");
        }
        int res = materialMapper.deleteMaterial(id);
        if(res!=1){
            throw new DeleteException("服务错误， 删除数据异常");
        }
        return this.getMaterials(userId);
    }

    private MaterialVo entityToVo(MaterialEntity entity) {
        MaterialVo vo = new MaterialVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setTitle(entity.getTitle());
        vo.setKnowledgePoint(entity.getKnowledgePoint());
        vo.setInstruction(entity.getInstruction());
        vo.setStatus(entity.getStatus());
        vo.setHtml(entity.getHtml());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        vo.setResource(new Resource());
        return vo;
    }
}
