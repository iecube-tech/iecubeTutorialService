package com.iecube.iecubetutorial.model.project.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.project.enmu.ProjectSource;
import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.mapper.ProjectMapper;
import com.iecube.iecubetutorial.model.project.qo.EditHtmlQo;
import com.iecube.iecubetutorial.model.project.service.ProjectService;
import com.iecube.iecubetutorial.model.project.vo.ProjectDetailVo;
import com.iecube.iecubetutorial.model.project.vo.ProjectVo;
import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import com.iecube.iecubetutorial.model.projectChild.service.ProjectChildService;
import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.model.s_materials.entity.SMaterial;
import com.iecube.iecubetutorial.model.s_materials.service.SMaterialService;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectChildService projectChildService;

    @Autowired
    private SMaterialService sMaterialService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MOutlineService mOutlineService;

    @Value("${resource-location}")
    private String outputDirectory;

    @Override
    public Project createProjectByMaterial(MaterialEntity materialEntity) {
        Project project = new Project();
        project.setUserId(materialEntity.getUserId());
        project.setName(materialEntity.getName());
        project.setTitle(materialEntity.getTitle());
        project.setKnowledgePoint(materialEntity.getKnowledgePoint());
        project.setSource(ProjectSource.Material.name());
        project.setMaterialId(materialEntity.getId());
        project.setCreateTime(materialEntity.getCreateTime().toInstant());
        String id = UUIDGenerator.generateUUID();
        project.setId(id);
        int res = projectMapper.insert(project);
        if(res!=1){
            throw new InsertException("创建工程失败");
        }
        return project;
    }

    @Override
    public Project getByMaterial(long materialId) {
        return projectMapper.getByMaterial(materialId);
    }

    @Override
    public Project getById(String id) {
        return projectMapper.getById(id);
    }

    @Override
    public ProjectDetailVo createProjectByCollection(long collectionId) {
        SMaterial sMaterial = sMaterialService.getBYId(collectionId);
        Project project = this.createProjectBySMaterial(sMaterial);
        Resource resource=null;
        try{
            resource = resourceService.copyResource(sMaterial.getResource());
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
        if(resource==null){
            throw new ServiceException("处理文件失败");
        }
        ProjectChild projectChild = projectChildService.createProjectChild(project.getId(),resource.getId());
        //ProjectVo
        ProjectVo projectVo = new ProjectVo();
        projectVo.setId(project.getId());
        projectVo.setName(project.getName());
        projectVo.setTitle(project.getTitle());
        projectVo.setKnowledgePoint(project.getKnowledgePoint());
        projectVo.setCreateTime(project.getCreateTime());
        projectVo.setType(project.getSource());
        projectVo.setStatus("DONE");
        //ProjectChildVo
        ProjectChildVo projectChildVo = new ProjectChildVo();
        projectChildVo.setId(projectChild.getId());
        projectChildVo.setVersion(projectChild.getVersion());
        projectChildVo.setCreateTime(projectChild.getCreateTime());
        projectChildVo.setResource(resourceService.getResourceById(projectChild.getResource()));
        // ProjectDetailVo
        ProjectDetailVo projectDetailVo = new ProjectDetailVo();
        projectDetailVo.setProject(projectVo);
        List<ProjectChildVo> list = new ArrayList<>();
        list.add(projectChildVo);
        projectDetailVo.setProjectChildren(list);
        return projectDetailVo;
    }

    @Override
    public List<ProjectDetailVo> getAccountProjects() {
        List<ProjectDetailVo> res = new ArrayList<>();
        List<ProjectVo> projectVos = projectMapper.getProjectVoByUserId(ThreadLocalUtil.getAccountId());
        projectVos.forEach(projectVo -> {
            ProjectDetailVo projectDetailVo = new ProjectDetailVo();
            projectDetailVo.setProject(projectVo);
            projectDetailVo.setMOutline(mOutlineService.getByProjectId(projectVo.getId()));
            List<ProjectChildVo> projectChildren = projectChildService.projectChildList(projectVo.getId());
            projectDetailVo.setProjectChildren(projectChildren);
            res.add(projectDetailVo);
        });
        return res;
    }

    @Override
    public List<ProjectDetailVo> deleteProject(String projectId) {
        Project project = projectMapper.getById(projectId);
        if(project==null){
            throw new DeleteException("没有相关数据");
        }
        if(!Objects.equals(project.getUserId(), ThreadLocalUtil.getAccountId())){
            throw new AuthException("无权操作");
        }
        int res = projectMapper.delete(projectId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return this.getAccountProjects();
    }

    @Override
    public ProjectDetailVo getProjectDetailVo(String projectId) {
        Project project = projectMapper.getById(projectId);
        if(project==null){
            throw new DeleteException("没有相关数据");
        }
        ProjectVo projectVo = new ProjectVo();
        projectVo.setId(project.getId());
        projectVo.setName(project.getName());
        projectVo.setTitle(project.getTitle());
        projectVo.setKnowledgePoint(project.getKnowledgePoint());
        projectVo.setCreateTime(project.getCreateTime());
        projectVo.setType(project.getSource());
        projectVo.setStatus("DONE");

        List<ProjectChildVo> projectChildren = projectChildService.projectChildList(projectId);
        ProjectDetailVo projectDetailVo = new ProjectDetailVo();
        projectDetailVo.setProject(projectVo);
        projectDetailVo.setMOutline(mOutlineService.getByProjectId(projectVo.getId()));
        projectDetailVo.setProjectChildren(projectChildren);
        return projectDetailVo;
    }

    @Override
    public List<ProjectChildVo> editHtml(EditHtmlQo editHtmlQo) {
        ProjectChild pc = projectChildService.getById(editHtmlQo.getPChildId());
        if(pc==null){
            throw new ServiceException("未找到相关数据");
        }
        if(pc.getSaved()==null){
            pc.setSaved(false);
        }
        // 判断是不是用户保存的版本
        if(pc.getSaved()){
            // 在用户已保存的版本上修改保存，检查有无修改内容，有修改内容保存新版本，没有修改内容返回空
            Resource resource = resourceService.getResourceById(pc.getResource());
            Path directory = Paths.get(outputDirectory);
            Path filePath = directory.resolve(resource.getFilename());
            try{
                if(isEqual(editHtmlQo.getHtmlBase64(),filePath.toString())){
                    // 文件没有修改
                    return null;
                }else{
                    Resource res = resourceService.writeHtmlToFile(editHtmlQo.getHtmlBase64());
                    Resource result = resourceService.saveResource(res);
                    // 创建新版本
                    projectChildService.createProjectChild(pc.getProjectId(), result.getId(), true);
                    return projectChildService.projectChildList(pc.getProjectId());
                }
            }catch (Exception e){
                throw new ServiceException(e.getMessage());
            }
        }else {
            //保存当前最新版本
            List<ProjectChildVo> projectChildList = projectChildService.projectChildList(pc.getProjectId());
            int userVersion=1;
            for (ProjectChildVo projectChildVo : projectChildList) {
                if(projectChildVo.getUserVersion()!=null){
                    userVersion+=1;
                }
            }
            pc.setUserVersion(userVersion);
            pc.setSaved(true);
            projectChildService.updateProjectChild(pc);
            return projectChildService.projectChildList(pc.getProjectId());
        }
//
    }

    private Project createProjectBySMaterial(SMaterial sMaterial) {
        Project project = new Project();
        project.setUserId(ThreadLocalUtil.getAccountId());
        project.setName(sMaterial.getName());
        project.setTitle(sMaterial.getTitle());
        project.setKnowledgePoint(sMaterial.getKnowledgePoint());
        project.setSource(ProjectSource.Collection.name());
        project.setSMaterialId(sMaterial.getId());
        project.setCreateTime(Instant.now());
        project.setId(UUIDGenerator.generateUUID());
        int res = projectMapper.insert(project);
        if(res!=1){
            throw new InsertException("创建工程失败");
        }
        // 创建ProjectChild
        return project;
    }


    public static boolean isEqual(String base64Str, String filePath) throws IOException {
        // 1. 快速校验：比较前端文件内容编码后和传递的文本是否一致
        byte[] fileContent = readFileToBytes(filePath);// 读取文件内容到字节数组
        String fileBase64 = Base64.getEncoder().encodeToString(fileContent);// 进行Base64编码
        return fileBase64.equals(base64Str);
    }

    /**
     * 将文件内容读取为字节数组
     * @param filePath 文件路径
     * @return 文件内容的字节数组
     * @throws IOException 可能的IO异常
     */
    private static byte[] readFileToBytes(String filePath) throws IOException {
        File file = new File(filePath);
        // 使用try-with-resources确保流自动关闭
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            // 读取文件内容到缓冲区
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }


}
