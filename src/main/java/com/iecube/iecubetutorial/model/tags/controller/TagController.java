package com.iecube.iecubetutorial.model.tags.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import com.iecube.iecubetutorial.model.tags.service.TagService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/o/tag")
@io.swagger.v3.oas.annotations.tags.Tag(name="标签")
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;

    @Operation(summary = "查询标签 [ADMIN,OPERATOR]")
    @ApiPermissions({"ADMIN", "OPERATOR"})
    @GetMapping
    public JsonResult<List<Tag>> getAllTags(){
        return new JsonResult<>(OK, tagService.getAllTags());
    }

    @Operation(summary = "新增标签 [ADMIN,OPERATOR]")
    @ApiPermissions({"ADMIN", "OPERATOR"})
    @PostMapping()
    public JsonResult<List<Tag>> addTag(@RequestBody Tag tag){
        tagService.addTag(tag);
        return new JsonResult<>(OK, tagService.getAllTags());
    }

    @Operation(summary = "删除标签 [ADMIN,OPERATOR]")
    @ApiPermissions({"ADMIN", "OPERATOR"})
    @DeleteMapping()
    public JsonResult<List<Tag>> delTag(@RequestBody Tag tag){
        tagService.deleteTag(tag);
        return new JsonResult<>(OK, tagService.getAllTags());
    }
}
