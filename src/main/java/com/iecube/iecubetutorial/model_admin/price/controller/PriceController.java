package com.iecube.iecubetutorial.model_admin.price.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sm/m")
public class PriceController extends BaseController {

    @Autowired
    private PriceUnitService priceUnitService;

    @Operation(summary = "查询定价 [ADMIN]", description = "role限制: [ADMIN]", tags = {"管理端定价"})
    @ApiPermissions({"ADMIN"})
    @GetMapping("/price/units")
    public JsonResult<List<PriceUnit>> priceUnits(){
        return new JsonResult<>(OK, priceUnitService.findAll());
    }

    // todo 修改定价
}
