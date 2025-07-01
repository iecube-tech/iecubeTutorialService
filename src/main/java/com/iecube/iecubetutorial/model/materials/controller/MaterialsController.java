package com.iecube.iecubetutorial.model.materials.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.qo.UpMaterialQo;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.model.materials.vo.MaterialVo;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialsController extends BaseController {

    @Autowired
    private MaterialService materialService;

    @Operation(summary = "生成讲义" ,hidden = true)
    @ApiPermissions({"USER_M","USER"})
    @PostMapping("/generate")
    public JsonResult<Void> generate(@RequestBody MaterialQo materialQo) {
        Long accountId = ThreadLocalUtil.getAccountId();
        materialService.generateMaterial(materialQo, accountId);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "请求用户生成的讲义", hidden = true)
    @ApiPermissions({"USER_M","USER"})
    @GetMapping("/created")
    public JsonResult<List<MaterialVo>> created(){
        Long accountId = ThreadLocalUtil.getAccountId();
        List<MaterialVo> vos = materialService.getMaterials(accountId);
        return new JsonResult<>(OK, vos);
    }

    @Operation(summary = "更新生成的讲义html文件内容", hidden = true)
    @ApiPermissions({"USER_M","USER"})
    @PostMapping("/update")
    public JsonResult<MaterialVo> updateMaterial(@RequestBody UpMaterialQo upMaterialQo) {
        Long accountId = ThreadLocalUtil.getAccountId();
        MaterialVo materialVo = materialService.updateMaterial(upMaterialQo, accountId);
        return new JsonResult<>(OK, materialVo);
    }

    @Operation(summary = "用户删除生成的讲义", hidden = true)
    @ApiPermissions({"USER_M","USER"})
    @DeleteMapping("/del/{id}")
    public JsonResult<List<MaterialVo>> delMaterial(@PathVariable Long id) {
        Long accountId = ThreadLocalUtil.getAccountId();
        List<MaterialVo> res = materialService.deleteMaterial(id, accountId);
        return new JsonResult<>(OK, res);
    }
}
