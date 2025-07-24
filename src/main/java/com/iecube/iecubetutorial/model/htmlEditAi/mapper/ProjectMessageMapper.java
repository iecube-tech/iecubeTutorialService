package com.iecube.iecubetutorial.model.htmlEditAi.mapper;

import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMessageMapper {
    int insert(ProjectMessage record);

    List<ProjectMessage> getByProject(String projectId);

    ProjectMessage getById(String id);

    int delete(String id);
}
