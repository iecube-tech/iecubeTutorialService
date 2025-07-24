package com.iecube.iecubetutorial.model.project.vo;

import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailVo {
    private ProjectVo project;
    private List<ProjectChildVo> projectChildren;
}
