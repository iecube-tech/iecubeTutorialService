package com.iecube.iecubetutorial.model_admin.user.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_admin.user.entity.AUserRole;
import com.iecube.iecubetutorial.model_admin.user.qo.AUserQo;
import com.iecube.iecubetutorial.model_admin.user.service.AUserRoleService;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/s/user")
public class SmUserController extends BaseController {

    @Autowired
    private AUserRoleService userRoleService;

    @Autowired
    private AUserService userService;

    @Operation(summary = "查询用户角色列表 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"})
    @ApiPermissions({"SUPER"})
    @GetMapping("/roles")
    public JsonResult<List<AUserRole>> getAllRole(){
        return new JsonResult<>(OK, userRoleService.roleList());
    }

    @Operation(summary = "查询所有用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"} )
    @ApiPermissions({"SUPER"})
    @GetMapping("/list")
    public JsonResult<List<AUser>> getAllUser(){
        return new JsonResult<>(OK, userService.GetAllUsers());
    }

    @Operation(summary = "添加用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"} )
    @PostMapping("/add")
    @ApiPermissions({"SUPER"})
    public JsonResult<AUser> addUser(@RequestBody AUserQo aUserQo){
        return new JsonResult<>(OK, userService.CreateUser(aUserQo,currentUserPhone()));
    }

    @Operation(summary = "更新用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"} )
    @ApiPermissions({"SUPER"})
    @PostMapping("/up")
    public JsonResult<AUser> updateUser(@RequestBody AUserQo aUserQo){
        return new JsonResult<>(OK, userService.UpdateUser(aUserQo,currentUserPhone()));
    }

    @Operation(summary = "删除用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"} )
    @ApiPermissions({"SUPER"})
    @DeleteMapping("/del")
    public JsonResult<List<AUser>> delUser(@RequestBody AUserQo aUserQo){
        return new JsonResult<>(OK, userService.deleteUser(aUserQo,currentUserPhone()));
    }

    @Operation(summary = "禁用用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"} )
    @ApiPermissions({"SUPER"})
    @PostMapping("/disable")
    public JsonResult<AUser> disable(@RequestBody AUserQo aUserQo){
        return new JsonResult<>(OK, userService.disableUser(aUserQo,currentUserPhone()));
    }

    @Operation(summary = "使能用户 [SUPER]", description = "role限制: [SUPER]", tags = {"管理端用户管理"})
    @ApiPermissions({"SUPER"})
    @PostMapping("/enable")
    public JsonResult<AUser> enable(@RequestBody AUserQo aUserQo){
        return new JsonResult<>(OK, userService.enableUser(aUserQo,currentUserPhone()));
    }
}

