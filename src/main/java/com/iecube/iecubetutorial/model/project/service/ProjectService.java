package com.iecube.iecubetutorial.model.project.service;

import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.qo.EditHtmlQo;
import com.iecube.iecubetutorial.model.project.vo.ProjectDetailVo;

import java.util.List;

public interface ProjectService {
    Project createProjectByMaterial(MaterialEntity materialEntity);

    Project getByMaterial(long materialId);

    Project getById(String id);

    ProjectDetailVo createProjectByCollection(long collectionId);

    List<ProjectDetailVo> getAccountProjects();

    List<ProjectDetailVo> deleteProject(String projectId);

    ProjectDetailVo getProjectDetailVo(String projectId);

    void editHtml(EditHtmlQo editHtmlQo);
}
