package com.iecube.iecubetutorial.model.materials.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.mOutline.clientService.W6ClientService;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import com.iecube.iecubetutorial.model.mOutline.wsHandler.WsHandler;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.qo.UpMaterialQo;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.model.materials.service.OneClickService;
import com.iecube.iecubetutorial.model.materials.vo.MaterialVo;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/material")
@Tag(name = "讲义相关")
public class MaterialsController extends BaseController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private OneClickService oneClickService;

    @Operation(summary = "生成讲义 [USER, USER_M]" )
    @ApiPermissions({"USER_M","USER"})
    @PostMapping("/generate")
    public JsonResult<Void> generate(@RequestBody MaterialQo materialQo) {
        oneClickService.oneClickGenMaterial(materialQo);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "请求用户生成的讲义 [USER, USER_M]  ***********将要弃用***********" )
    @ApiPermissions({"USER_M","USER"})
    @GetMapping("/created")
    public JsonResult<List<MaterialVo>> created(){
        Long accountId = ThreadLocalUtil.getAccountId();
        List<MaterialVo> vos = materialService.getMaterials(accountId);
        return new JsonResult<>(OK, vos);
    }

    @Operation(summary = "更新生成的讲义html文件内容 [USER, USER_M]  ***********将要弃用*********** ")
    @ApiPermissions({"USER_M","USER"})
    @PostMapping("/update")
    public JsonResult<MaterialVo> updateMaterial(@RequestBody UpMaterialQo upMaterialQo) {
        Long accountId = ThreadLocalUtil.getAccountId();
        MaterialVo materialVo = materialService.updateMaterial(upMaterialQo, accountId);
        return new JsonResult<>(OK, materialVo);
    }

    @Operation(summary = "用户删除生成的讲义[USER, USER_M]  ***********将要弃用***********")
    @ApiPermissions({"USER_M","USER"})
    @DeleteMapping("/del/{id}")
    public JsonResult<List<MaterialVo>> delMaterial(@PathVariable Long id) {
        Long accountId = ThreadLocalUtil.getAccountId();
        List<MaterialVo> res = materialService.deleteMaterial(id, accountId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/all")
    @Operation(summary = "获取系统中所有生成的案例 [ADMIN, OPERATOR]")
    @ApiPermissions({"ADMIN","OPERATOR"})
    public JsonResult<List<MaterialVo>> allMaterials() {
        return new JsonResult<>(OK, materialService.getAllMaterials());
    }
}
