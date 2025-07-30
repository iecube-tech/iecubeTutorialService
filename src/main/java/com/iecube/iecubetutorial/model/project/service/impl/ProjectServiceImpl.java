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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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
    public void editHtml(EditHtmlQo editHtmlQo) {
        ProjectChild pc = projectChildService.getById(editHtmlQo.getPChildId());
        Resource resource = resourceService.getResourceById(pc.getResource());
        Resource res = resourceService.updateResource(editHtmlQo.getHtmlBase64(),resource);
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


}
