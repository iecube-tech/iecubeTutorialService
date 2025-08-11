package com.iecube.iecubetutorial.model.project.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.project.qo.EditHtmlQo;
import com.iecube.iecubetutorial.model.project.service.ProjectService;
import com.iecube.iecubetutorial.model.project.vo.ProjectDetailVo;
import com.iecube.iecubetutorial.model.project.vo.ProjectVo;
import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@Tag(name = "工程")
public class projectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    // 获取列表 projectVoList
    @GetMapping()
    @Operation(summary = "请求工程列表")
    @ApiPermissions({"USER_M", "USER"})
    public JsonResult<List<ProjectDetailVo>> getProjectVoListByUser(){
        return new JsonResult<>(OK, projectService.getAccountProjects());
    }

    @DeleteMapping
    @Operation(summary = "删除工程")
    @ApiPermissions({"USER_M", "USER"})
    public JsonResult<List<ProjectDetailVo>> delProject(String projectId){
        return new JsonResult<>(OK, projectService.deleteProject(projectId));
    }

    // 获取详细信息 projectDetailVo
    @GetMapping("/detail")
    @Operation(summary = "请求工程的详细信息")
    @ApiPermissions({"USER_M", "USER"})
    public JsonResult<ProjectDetailVo> getProjectVoList(String projectId){
        return new JsonResult<>(OK, projectService.getProjectDetailVo(projectId));
    }

    @PostMapping()
    @Operation(summary = "根据案例集中案例创建出一个新的工程")
    @ApiPermissions({"USER_M", "USER"})
    public JsonResult<ProjectDetailVo> createProjectBySMaterial(long collection){
        ProjectDetailVo projectDetailVo = projectService.createProjectByCollection(collection);
        return new JsonResult<>(OK, projectDetailVo);
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑保存ProjectChild的html文件内容")
    @ApiPermissions({"USER_M", "USER"})
    public JsonResult<List<ProjectChildVo>> editHtml(@RequestBody EditHtmlQo editHtmlQo){
        return new JsonResult<>(OK,projectService.editHtml(editHtmlQo));
    }

}
