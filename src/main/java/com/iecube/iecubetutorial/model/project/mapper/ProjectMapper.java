package com.iecube.iecubetutorial.model.project.mapper;

import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.vo.ProjectVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
    int insert(Project project);

    List<Project> getByCreator(Long userId);

    Project getById(String id);

    Project getByMaterial(Long materialId);

    int delete(String id);

    List<ProjectVo> getProjectVoByUserId(Long userId);
}
