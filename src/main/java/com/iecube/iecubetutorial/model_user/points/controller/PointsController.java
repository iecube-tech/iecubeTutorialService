package com.iecube.iecubetutorial.model_user.points.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.model_user.points.vo.YearMonthConsumptionResponse;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/su/point")
@Tag(name = "用户积分查询")
public class PointsController extends BaseController {
    @Autowired
    private PointsService pointsService;

    @GetMapping("/valid")
    @ApiPermissions({"USER","USER_M"})
    @Operation(summary = "用户获取剩余积分 [USER,USER_M]")
    public JsonResult<Points> getPointsValid() {
        return new JsonResult<>(OK, pointsService.getPointsValidByAccount());
    }

    @GetMapping("/consume")
    @ApiPermissions({"USER","USER_M"})
    @Operation(summary = "用户获取总消耗积分及账单 [USER,USER_M]")
    public  JsonResult<ConsumePointVo> getConsumePointVo(){
        return new JsonResult<>(OK, pointsService.getConsumePointByAccount());
    }


    @GetMapping("/bill")
    @ApiPermissions({"USER","USER_M"})
    @Operation(summary = "用户按年月获取账单记录 [USER,USER_M]")
    public JsonResult<YearMonthConsumptionResponse> getYearMonthConsumptionResponse(){
        return new JsonResult<>(OK, pointsService.getAllConsumptionsGroupedByYearMonth());
    }
}
