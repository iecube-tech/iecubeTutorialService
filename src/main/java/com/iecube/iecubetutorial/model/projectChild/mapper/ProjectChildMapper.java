package com.iecube.iecubetutorial.model.projectChild.mapper;

import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectChildMapper {

    int insert(ProjectChild record);

    List<ProjectChild> getByProject(String projectId);

    ProjectChild getById(String id);
}
