package com.iecube.iecubetutorial.model.materials.service.impl;

import com.iecube.iecubetutorial.exception.*;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.exception.FailedToCreateTaskException;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialChatMapper;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialMapper;
import com.iecube.iecubetutorial.model.materials.qo.UpMaterialQo;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.model.materials.vo.MaterialVo;
import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.service.ProjectService;
import com.iecube.iecubetutorial.model.projectChild.service.ProjectChildService;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.mapper.ResourceMapper;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.points.exception.PointsNotEnoughException;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectChildService projectChildService;

    @Autowired
    private MOutlineService mOutlineService;

    private final BlockingQueue<MaterialChat> NewConnectTask;

    public MaterialServiceImpl(BlockingQueue<MaterialChat> NewConnectTask){
        this.NewConnectTask=NewConnectTask;
    }

    @Override
    public void genMaterialByOutline(String mOutlineId){
        MOutline mOutline = mOutlineService.getById(mOutlineId);
        Account account = accountService.getAccount(mOutline.getCreator());
        MaterialEntity material = new MaterialEntity(); // material
        material.setUserId(mOutline.getCreator());
        material.setName(mOutline.getName());
        material.setTitle(mOutline.getTitle());
        material.setKnowledgePoint(mOutline.getKnowledgePoint());
        material.setStatus(MaterialStatus.NOTReady.getStatus());
        material.setDeleted(0);
        material.setCreateTime(new Date());
        material.setInstruction(null);
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
        // 创建工程
        Project project = projectService.createProjectByMaterial(material);
        mOutline.setProjectId(project.getId());
        MOutline mRes = mOutlineService.updateMOutline(mOutline);
        // 数据准备工作完毕
        //和 生产消费者模型 AI建立websocket连接，处理生成任务  连接之后 material.setStatus(MaterialStatus.GENERATING.getStatus()); 更新状态
        if(!pointsService.pointsEnough(account)){
            throw new PointsNotEnoughException("余额不足");
        }
        // 创建新的任务：开始准备接收AI对话，接收，并处理AI消息
        try {
            NewConnectTask.put(materialChat);
            log.info("创建AI任务：交由AI处理：{}",chatId);
        } catch (InterruptedException e) {
            throw new FailedToCreateTaskException(e.getMessage());
        }
        // 调用AI模型，给AI模型下发指令
        w6ApiService.usePageMaker(chatId, material.getTitle(), material.getKnowledgePoint(), mOutline.getOutline());
    }

    @Override
    public void oneClickGen(MOutline mOutline, MaterialChat materialChat){
        // 创建新的任务：开始准备接收AI对话，接收，并处理AI消息
        try {
            NewConnectTask.put(materialChat);
            log.info("一键生成：创建讲义生成任务：交由AI处理：{}", materialChat.getChatId());
        } catch (InterruptedException e) {
            throw new FailedToCreateTaskException(e.getMessage());
        }
        // 调用AI模型，给AI模型下发指令
        w6ApiService.usePageMaker(materialChat.getChatId(), mOutline.getTitle(), mOutline.getKnowledgePoint(), mOutline.getOutline());
    }

    @Override
    public List<MaterialVo> getMaterials(Long accountId) {
        List<MaterialEntity> entities = materialMapper.getMaterials(accountId);
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
        if(materialEntity.getStatus().equals(MaterialStatus.DONE.getStatus())){
            Resource resource = resourceService.writeHtmlToFile(materialEntity.getHtml());
            Resource nRe =  resourceService.saveResource(resource);
            materialEntity.setResource(nRe.getId());
            // 创建工程的v1版本
            Project project = projectService.getByMaterial(materialEntity.getId());
            projectChildService.createProjectChild(project.getId(), materialEntity.getResource());
        }
        materialMapper.updateMaterial(materialEntity);
    }

    @Override
    public MaterialVo updateMaterial(UpMaterialQo upMaterialQo, Long accountId) {
        if(upMaterialQo.getId()==null){
            throw new AuthException("没有权限");
        }
        MaterialEntity materialEntity = materialMapper.getMaterial(upMaterialQo.getId());
        if(!accountId.equals(materialEntity.getUserId())){
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
    public List<MaterialVo> deleteMaterial(Long id, Long accountId) {
        if(id==null){
            throw new AuthException("没有权限");
        }
        MaterialEntity materialEntity = materialMapper.getMaterial(id);
        if(!accountId.equals(materialEntity.getUserId())){
            throw new AuthException("没有权限");
        }
        int res = materialMapper.deleteMaterial(id);
        if(res!=1){
            throw new DeleteException("服务错误， 删除数据异常");
        }
        return this.getMaterials(accountId);
    }

    @Override
    public String decrypt(String encryptedText) {
        byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @Override
    public List<MaterialVo> getAllMaterials() {
        List<MaterialEntity> entities = materialMapper.getAllMaterials();
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
