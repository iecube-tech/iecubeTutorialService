package com.iecube.iecubetutorial.model.mOutline.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outline")
@Tag(name = "大纲")
public class MOutlineController extends BaseController {
    @Autowired
    private MOutlineService mOutlineService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private WsManager wsManager;

    @PostMapping()
    @Operation(summary = "生成大纲 [USER_M, USER]", description = "调用接口会返回一个MOutline对象，根据其中的id字段结合baseWsUrl建立websocket连接，接收返回的大纲stream流")
    @ApiPermissions({"USER_M","USER"})
    public JsonResult<MOutline> genMOutline(@RequestBody MaterialQo materialQo) {
        MOutline mOutline = mOutlineService.genMOutline(materialQo, false, null);
        wsManager.lookOutline().put(mOutline.getChatId(), mOutline);
        return new JsonResult<>(OK, mOutline);
    }

    @PostMapping("/update")
    @Operation(summary = "编辑大纲 [USER_M, USER]")
    @ApiPermissions({"USER_M","USER"})
    public JsonResult<MOutline> updateMOutline(@RequestBody MOutline mOutline) {
        return new JsonResult<>(OK, mOutlineService.updateMOutline(mOutline));
    }

    @PostMapping("/materials/{mOutlineId}")
    @Operation(summary = "根据大纲生成讲义 [USER_M, USER]")
    @ApiPermissions({"USER_M","USER"})
    public JsonResult<Void> gen(@PathVariable String mOutlineId){
        materialService.genMaterialByOutline(mOutlineId);
        return new JsonResult<>(OK);
    }
}
