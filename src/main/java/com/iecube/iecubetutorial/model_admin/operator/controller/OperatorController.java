package com.iecube.iecubetutorial.model_admin.operator.controller;


import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.operator.qo.AddUUserQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.OrgSecQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.RechargeQo;
import com.iecube.iecubetutorial.model_admin.operator.service.OperatorService;
import com.iecube.iecubetutorial.model_admin.operator.vo.OrganizationVo;
import com.iecube.iecubetutorial.model_admin.operator.vo.userTypeVo;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.enmu.UserType;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import com.iecube.iecubetutorial.model_user.organization_top.qo.OrgTopQo;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/sm/o/")
@Tag(name="运营操作")
public class OperatorController extends BaseController {

    @Autowired
    private OperatorService operatorService;

    @Operation(summary = "查询审核人员 [ADMIN,OPERATOR]", description = "role限制：[ADMIN,OPERATOR]")
    @ApiPermissions({"ADMIN","OPERATOR"})
    @GetMapping("/approves")
    public JsonResult<List<AUser>> getAdminUsers(){
        List<AUser> result = operatorService.getAdminUsers();
        return new JsonResult<>(OK, result);
    }

    @Operation(summary = "查询一级组织 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/top")
    public JsonResult<List<OrgTop>> getOrgTops(){
        return new JsonResult<>(OK, operatorService.getOrgTops());
    }

    @Operation(summary = "修改一级组织 [OPERATOR]", description = "role限制：[OPERATOR]，只允许修改名称(name字段)")
    @ApiPermissions({"OPERATOR"})
    @PostMapping("/org/top")
    public JsonResult<OrgTop> updateOrgTop(@RequestBody OrgTop orgTop){
        if (orgTop.getId()==null){
            throw new UpdateException("参数异常");
        }
        return new JsonResult<>(OK, operatorService.updateOrgTop(orgTop));
    }

    @Operation(summary = "删除一级组织 [OPERATOR]", description = "role限制：[OPERATOR]，子组织不为空时无法删除")
    @ApiPermissions({"OPERATOR"})
    @DeleteMapping("/org/top")
    public JsonResult<List<OrgTop>> delOrgTop(@RequestBody OrgTop orgTop){
        if (orgTop.getId()==null){
            throw new UpdateException("参数异常");
        }
        return new JsonResult<>(OK, operatorService.removeOrgTop(orgTop.getId()));
    }

    @Operation(summary = "根据一级组织查询二级组织 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/sec/{topId}")
    public JsonResult<List<OrgSec>> getOrgSecs(@PathVariable long topId){
        return new JsonResult<>(OK, operatorService.getOrgSecs(topId));
    }

    @Operation(summary = "查询所有组织列表 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/list")
    public JsonResult<List<OrganizationVo>> getAllOrg(){
        return new JsonResult<>(OK,operatorService.getAllOrganizations());
    }

    @Operation(summary = "查询可选的组织类型,用户类型 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/type")
    public JsonResult<List<userTypeVo>> getUserType(){
        List<userTypeVo> res = Arrays.stream(UserType.values())
                .map(type -> new userTypeVo(type.name(), type.getLabel()))
                .toList();
        return new JsonResult<>(OK, res);
    }

    @Operation(summary = "根据二级组织查询可用积分 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/points/valid/{oSecId}")
    public JsonResult<Points> getOSecValidPoint(@PathVariable Long oSecId){
        return new JsonResult<>(OK, operatorService.getPointsValid(oSecId));
    }


    @Operation(summary = "根据二级组织查询已花费积分 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/points/consumed/{oSecId}")
    public JsonResult<ConsumePointVo> getConsumePoint(@PathVariable Long oSecId){
        return new JsonResult<>(OK, operatorService.getConsumePoint(oSecId));
    }

    @Operation(summary = "创建一级组织 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @PostMapping("/org/top/create")
    public JsonResult<List<OrganizationVo>> createOrgTop(@RequestBody OrgTopQo orgTopQo){
        List<OrganizationVo> res = operatorService.createOrganizationTop(orgTopQo);
        return new JsonResult<>(OK,res);
    }

    @Operation(summary = "创建二级组织 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @PostMapping("/org/sec/create")
    public JsonResult<Approval> createOrgSec(@RequestBody OrgSecQo orgSecQo){
        Approval approval = operatorService.createOrganizationSec(orgSecQo);
        return new JsonResult<>(OK,approval);
    }

    @Operation(summary = "向二级组织添加人员 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @PostMapping("/org/sec/users/add")
    public JsonResult<Approval> addUsersToOSec(@RequestBody AddUUserQo addUUserQo){
        Approval approval = operatorService.addUsersToOrgSec(addUUserQo);
        return new JsonResult<>(OK,approval);
    }

    @Operation(summary = "根据二级组织查询人员账户 [OPERATOR]", description = "role限制：[OPERATOR]")
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/org/users/{oSecId}")
    public JsonResult<List<AccountVo>> getOSecAccounts(@PathVariable Long oSecId){
        return new JsonResult<>(OK,operatorService.getOSecAccountVos(oSecId));
    }

    @Operation(summary = "计算rmb可以换多少积分 [OPERATOR]", description = "role限制：[OPERATOR]", tags = {"积分"})
    @ApiPermissions({"OPERATOR"})
    @GetMapping("/points/compute")
    public JsonResult<Double> computePoints(double rmb){
        return new JsonResult<>(OK,operatorService.computePoints(rmb));
    }

    @Operation(summary = "二级组织积分充值 [OPERATOR]", description = "role限制：[OPERATOR]", tags = {"积分"})
    @ApiPermissions({"OPERATOR"})
    @PostMapping("/points/recharge/org")
    public JsonResult<Approval> recharge(@RequestBody RechargeQo rechargeQo){
        return new JsonResult<>(OK,operatorService.recharge(rechargeQo));
    }


}
