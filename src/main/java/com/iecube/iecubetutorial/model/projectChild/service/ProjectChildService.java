package com.iecube.iecubetutorial.model.projectChild.service;

import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;

import java.util.List;

public interface ProjectChildService {

    ProjectChild createProjectChild(String projectId, Long ResourceId);

    List<ProjectChildVo> projectChildList(String projectId);

    ProjectChild getById(String id);
}
