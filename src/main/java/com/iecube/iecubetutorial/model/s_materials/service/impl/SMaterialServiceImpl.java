package com.iecube.iecubetutorial.model.s_materials.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialMapper;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.exception.HandelFileFailedException;
import com.iecube.iecubetutorial.model.resource.mapper.ResourceMapper;
import com.iecube.iecubetutorial.model.s_m_t.entity.SMaterialTag;
import com.iecube.iecubetutorial.model.s_m_t.mapper.SMaterialTagMapper;
import com.iecube.iecubetutorial.model.s_materials.entity.SMaterial;
import com.iecube.iecubetutorial.model.s_materials.mapper.SMaterialMapper;
import com.iecube.iecubetutorial.model.s_materials.qo.ExportQo;
import com.iecube.iecubetutorial.model.s_materials.qo.UploadQo;
import com.iecube.iecubetutorial.model.s_materials.service.SMaterialService;
import com.iecube.iecubetutorial.model.s_materials.vo.SMaterialVo;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class SMaterialServiceImpl implements SMaterialService {

    @Autowired
    private SMaterialMapper sMaterialMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private SMaterialTagMapper sMaterialTagMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Value("${resource-location}")
    private String outputDirectory;


    @Override
    public List<SMaterialVo> exportFromMaterial(ExportQo exportQo) {
        MaterialEntity material = materialMapper.getMaterial(exportQo.getMaterialId());
        SMaterial sMaterial = MaterialEntitytoSMaterial(material);
        sMaterial.setCover(exportQo.getCover());
        sMaterial.setCreateTime(Instant.now());
        sMaterial.setLastOperateTime(Instant.now());
        sMaterial.setCreator(ThreadLocalUtil.getPhone());
        sMaterial.setLastOperator(ThreadLocalUtil.getPhone());
        int res = sMaterialMapper.Insert(sMaterial);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        // 添加标签
        List<SMaterialTag> sMaterialTags = new ArrayList<>();
        exportQo.getTags().forEach(tag -> {
            SMaterialTag sMaterialTag = new SMaterialTag();
            sMaterialTag.setSMaterialId(sMaterial.getId());
            sMaterialTag.setTagId(tag.getId());
            sMaterialTags.add(sMaterialTag);
        });
        int res1 = sMaterialTagMapper.batchInsert(sMaterialTags);
        if(res1!=sMaterialTags.size()){
            throw new InsertException("新增数据异常");
        }
        return this.getAllMaterials();
    }

    private SMaterial MaterialEntitytoSMaterial(MaterialEntity materialEntity) {
        SMaterial sMaterial = new SMaterial();
        sMaterial.setTitle(materialEntity.getTitle());
        sMaterial.setName(materialEntity.getName());
        sMaterial.setKnowledgePoint(String.valueOf(materialEntity.getKnowledgePoint()));
        sMaterial.setInstruction(materialEntity.getInstruction());
        sMaterial.setHtml(materialEntity.getHtml());
        sMaterial.setMaterialId(materialEntity.getId());
        sMaterial.setResource(materialEntity.getResource());
        sMaterial.setRemoved(0);
        sMaterial.setOutline(null);
        return sMaterial;
    }

    @Override
    public List<SMaterialVo> uploadSMaterial(UploadQo uploadQo) {
        Resource file = resourceMapper.getResource(uploadQo.getFile());
        Resource cover = resourceMapper.getResource(uploadQo.getCover());
        if(file==null || !file.getType().equals("text/html")){
            throw new ServiceException("不支持的讲义格式或未找到文件");
        }
        if(cover == null){
            throw new ServiceException("不支持的封面格式或未找到文件");
        }
        String filePath = Paths.get(outputDirectory, file.getFilename()).toString();
        SMaterial sMaterial = new SMaterial();
        sMaterial.setResource(uploadQo.getFile());
        sMaterial.setCover(uploadQo.getCover());
        sMaterial.setTitle(uploadQo.getTitle());
        sMaterial.setName(uploadQo.getName());
        sMaterial.setKnowledgePoint(uploadQo.getKnowledgePoint());
        sMaterial.setOutline(uploadQo.getOutline());
        sMaterial.setInstruction(uploadQo.getInstruction());
        sMaterial.setCreator(ThreadLocalUtil.getPhone());
        sMaterial.setCreateTime(Instant.now());
        sMaterial.setLastOperateTime(Instant.now());
        sMaterial.setLastOperator(ThreadLocalUtil.getPhone());
        try{
            String htmlContent = readFile(filePath);
            // 对完整HTML内容进行Base64编码
            String base64Encoded = encodeToBase64(htmlContent);
            sMaterial.setHtml(base64Encoded);
        }catch (IOException e){
            throw new HandelFileFailedException("读取文件内容失败");
        }
        int res = sMaterialMapper.Insert(sMaterial);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        // 添加标签
        List<SMaterialTag> sMaterialTags = new ArrayList<>();
        uploadQo.getTags().forEach(tag -> {
            SMaterialTag sMaterialTag = new SMaterialTag();
            sMaterialTag.setSMaterialId(sMaterial.getId());
            sMaterialTag.setTagId(tag.getId());
            sMaterialTags.add(sMaterialTag);
        });
        int res1 = sMaterialTagMapper.batchInsert(sMaterialTags);
        if(res1!=sMaterialTags.size()){
            throw new InsertException("新增数据异常");
        }
        return this.getAllMaterials();
    }

    @Override
    public List<SMaterialVo> deleteSMaterial(Long id) {
        int res = sMaterialMapper.deleteByPrimaryKey(id);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return this.getAllMaterials();
    }

    @Override
    public List<SMaterialVo> getAllMaterials() {
        List<SMaterial> sMaterialList = sMaterialMapper.selectAll();
        List<SMaterialVo> sMaterialVoList = new ArrayList<>();
        sMaterialList.forEach(sMaterial -> {
            sMaterialVoList.add(entityToVo(sMaterial));
        });
        return sMaterialVoList;
    }

    @Override
    public List<SMaterialVo> getMaterialsByKeyWords(String title, String knowledgePoint) {
        List<SMaterial> sMaterialList = sMaterialMapper.selectByKeyword(title, knowledgePoint);
        List<SMaterialVo> sMaterialVoList = new ArrayList<>();
        sMaterialList.forEach(sMaterial -> {
            sMaterialVoList.add(entityToVo(sMaterial));
        });
        return sMaterialVoList;
    }

    @Override
    public List<SMaterialVo> getMaterialsByTag(Tag tag) {
        List<SMaterial> sMaterialList = sMaterialMapper.selectByTag(tag.getId());
        List<SMaterialVo> sMaterialVoList = new ArrayList<>();
        sMaterialList.forEach(sMaterial -> {
            sMaterialVoList.add(entityToVo(sMaterial));
        });
        return sMaterialVoList;
    }

    @Override
    public SMaterial getBYId(Long id) {
        return sMaterialMapper.selectByPrimaryKey(id);
    }

    @Override
    public SMaterialVo getById(Long id) {
        SMaterial sMaterial = sMaterialMapper.selectByPrimaryKey(id);
        return entityToVo(sMaterial);
    }

    // 读取文件内容
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // 对字符串进行Base64编码
    private static String encodeToBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }


    private SMaterialVo entityToVo(SMaterial sMaterial) {
        SMaterialVo sMaterialVo = new SMaterialVo();
        sMaterialVo.setId(sMaterial.getId());
        sMaterialVo.setTitle(sMaterial.getTitle());
        sMaterialVo.setName(sMaterial.getName());
        sMaterialVo.setKnowledgePoint(sMaterial.getKnowledgePoint());
        sMaterialVo.setInstruction(sMaterial.getInstruction());
        sMaterialVo.setHtml(sMaterial.getHtml());
        sMaterialVo.setOutline(sMaterial.getOutline());
        Resource cover = resourceMapper.getResource(sMaterial.getCover());
        sMaterialVo.setCover(cover);
        Resource file = resourceMapper.getResource(sMaterial.getResource());
        sMaterialVo.setFile(file);
        List<Tag> tags = sMaterialTagMapper.getTagsBySMaterialId(sMaterial.getId());
        sMaterialVo.setTags(tags);
        return sMaterialVo;
    }
}
