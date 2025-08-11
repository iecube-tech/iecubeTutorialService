package com.iecube.iecubetutorial.model.projectChild.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.project.vo.ProjectDetailVo;
import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import com.iecube.iecubetutorial.model.projectChild.mapper.ProjectChildMapper;
import com.iecube.iecubetutorial.model.projectChild.service.ProjectChildService;
import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectChildServiceImpl implements ProjectChildService {

    @Autowired
    private ProjectChildMapper projectChildMapper;

    @Autowired
    private ResourceService resourceService;

    @Override
    public ProjectChild createProjectChild(String projectId, Long ResourceId) {
        List<ProjectChild> projectChildList = projectChildMapper.getByProject(projectId);
        ProjectChild projectChild = new ProjectChild();
        projectChild.setProjectId(projectId);
        projectChild.setId(UUIDGenerator.generateUUID());
        projectChild.setSaved(false);
        if(projectChildList.isEmpty()){
            projectChild.setVersion(1);
            projectChild.setSaved(true);
            projectChild.setUserVersion(1);
        }else{
            projectChild.setVersion(projectChildList.get(projectChildList.size()-1).getVersion() + 1);
        }
        projectChild.setResource(ResourceId);
        projectChild.setCreateTime(Instant.now());
        projectChild.setRemoved(0);
        int res = projectChildMapper.insert(projectChild);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return projectChild;
    }

    @Override
    public ProjectChild createProjectChild(String projectId, Long ResourceId, boolean saved) {
        List<ProjectChild> projectChildList = projectChildMapper.getByProject(projectId);
        ProjectChild projectChild = new ProjectChild();
        projectChild.setProjectId(projectId);
        projectChild.setId(UUIDGenerator.generateUUID());
        if(projectChildList.isEmpty()){
            projectChild.setVersion(1);
            projectChild.setSaved(Boolean.TRUE);
            projectChild.setUserVersion(1);
        }else{
            projectChild.setVersion(projectChildList.get(projectChildList.size()-1).getVersion() + 1);
        }
        projectChild.setResource(ResourceId);
        projectChild.setCreateTime(Instant.now());
        projectChild.setRemoved(0);
        if(saved){
            int userVersion = 1;
            for(ProjectChild pc : projectChildList){
                if(pc.getUserVersion()!=null){
                    userVersion+=1;
                }
            }
            projectChild.setUserVersion(userVersion);
        }
        projectChild.setSaved(saved);
        int res = projectChildMapper.insert(projectChild);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return projectChild;
    }

    @Override
    public List<ProjectChildVo> projectChildList(String projectId) {
        List<ProjectChild> projectChildList = projectChildMapper.getByProject(projectId);
        List<ProjectChildVo> projectChildVoList = new ArrayList<>();
        for(ProjectChild projectChild : projectChildList){
            ProjectChildVo projectChildVo = new ProjectChildVo();
            projectChildVo.setId(projectChild.getId());
            projectChildVo.setVersion(projectChild.getVersion());
            projectChildVo.setSaved(projectChild.getSaved()==null?Boolean.FALSE:projectChild.getSaved());
            projectChildVo.setUserVersion(projectChild.getUserVersion()==null?null:projectChild.getUserVersion());
            projectChildVo.setCreateTime(projectChild.getCreateTime());
            Resource resource = resourceService.getResourceById(projectChild.getResource());
            projectChildVo.setResource(resource);
            projectChildVoList.add(projectChildVo);
        }
        return projectChildVoList;
    }

    @Override
    public ProjectChild getById(String id) {
        return projectChildMapper.getById(id);
    }

    @Override
    public void updateProjectChild(ProjectChild newPc) {
        projectChildMapper.Update(newPc);
    }


}
