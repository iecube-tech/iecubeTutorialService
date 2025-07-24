package com.iecube.iecubetutorial.model.s_materials.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.s_materials.qo.ExportQo;
import com.iecube.iecubetutorial.model.s_materials.qo.UploadQo;
import com.iecube.iecubetutorial.model.s_materials.service.SMaterialService;
import com.iecube.iecubetutorial.model.s_materials.vo.SMaterialVo;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/collection")
@io.swagger.v3.oas.annotations.tags.Tag(name = "案例集")
public class SMaterialsController extends BaseController {

    @Autowired
    private SMaterialService sMaterialService;

    @PostMapping("/export")
    @Operation(summary = "从讲义导入到案例集 [OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    public JsonResult<List<SMaterialVo>> export(@RequestBody ExportQo exportQo){
        return new JsonResult<>(OK, sMaterialService.exportFromMaterial(exportQo));
    }


    @PostMapping("/upload")
    @Operation(summary = "上传到案例集 [OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    public JsonResult<List<SMaterialVo>> upload(@RequestBody UploadQo uploadQo){
        return new JsonResult<>(OK, sMaterialService.uploadSMaterial(uploadQo));
    }

    @DeleteMapping("/del/{id}")
    @Operation(summary = "删除 [OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    public JsonResult<List<SMaterialVo>> delete(@PathVariable Long id){
        return new JsonResult<>(OK, sMaterialService.deleteSMaterial(id));
    }

    @GetMapping
    @Operation(summary = "获取案例集中所有的内容 [OPERATOR, USER_M, USER]")
    @ApiPermissions({"OPERATOR", "USER_M", "USER"})
    public JsonResult<List<SMaterialVo>> getAllMaterials(){
        return new JsonResult<>(OK, sMaterialService.getAllMaterials());
    }

    @GetMapping("/find")
    @Operation(summary = "根据关键词查找 [OPERATOR, USER_M, USER]")
    @ApiPermissions({"OPERATOR", "USER", "USER_M"})
    public JsonResult<List<SMaterialVo>> getMaterialsByKeyWords(String title, String knowledgePoint ){
        if(title==null){
            title = "";
        }
        if(knowledgePoint==null){
            knowledgePoint = "";
        }
        if(title.isEmpty() && knowledgePoint.isEmpty()){
            return new JsonResult<>(OK, new ArrayList<>());
        }
        return new JsonResult<>(OK, sMaterialService.getMaterialsByKeyWords(title, knowledgePoint));
    }

    @PostMapping("/find")
    @Operation(summary = "根据标签查找 [OPERATOR, USER_M, USER]")
    @ApiPermissions({"OPERATOR", "USER", "USER_M"})
    public JsonResult<List<SMaterialVo>> getMaterialsByTag(@RequestBody Tag tag){
        return new JsonResult<>(OK, sMaterialService.getMaterialsByTag(tag));
    }

    @GetMapping("{id}")
    @Operation(summary = "根据Id查找案例集中的案例 [OPERATOR, USER_M, USER]")
    @ApiPermissions({"OPERATOR", "USER", "USER_M"})
    public JsonResult<SMaterialVo> getMaterialById(@PathVariable Long id){
        return new JsonResult<>(OK, sMaterialService.getById(id));
    }


}
