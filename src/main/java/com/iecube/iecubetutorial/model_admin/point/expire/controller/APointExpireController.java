package com.iecube.iecubetutorial.model_admin.point.expire.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model_admin.point.expire.entity.ExpireDays;
import com.iecube.iecubetutorial.model_admin.point.expire.service.APointExpireService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sm/m/point")
public class APointExpireController extends BaseController {

    @Autowired
    private APointExpireService aPointExpireService;

    @GetMapping("/expire/days")
    @Operation(summary = "积分有效期 [ADMIN]",tags = {"管理端定价"})
    @ApiPermissions({"ADMIN"})
    public JsonResult<Integer> getExpireDays() {
        return new JsonResult<>(OK, aPointExpireService.getExpireDays());
    }
}
