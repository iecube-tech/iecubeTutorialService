package com.iecube.iecubetutorial.model_admin.approval.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approval.service.ApprovalService;
import com.iecube.iecubetutorial.model_admin.approval.approvalType.entity.ApprovalType;
import com.iecube.iecubetutorial.model_admin.approval.approvalType.service.ApprovalTypeService;
import com.iecube.iecubetutorial.model_admin.approval.qo.ApproveQo;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/o")
@Tag(name="审批")
public class ApprovalController extends BaseController {

    @Autowired
    private ApprovalTypeService approvalTypeService;

    @Autowired
    private ApprovalService approvalService;

    @Operation(summary = "可选的审批类型 [ADMIN, OPERATOR]", description = "role限制：[ADMIN, OPERATOR]")
    @ApiPermissions({"ADMIN", "OPERATOR"})
    @GetMapping("/approval/types")
    public JsonResult<List<ApprovalType>> getApprovalTypes() {
        return new JsonResult<>(OK, approvalTypeService.getAll());
    }

    @Operation(summary = "查询我的审批 [ADMIN]", description = "role限制：[ADMIN]")
    @ApiPermissions({"ADMIN"})
    @GetMapping("/approval/my/approval")
    public JsonResult<List<Approval>> getByApprover() {
        return new JsonResult<>(OK, approvalService.getByApprover());
    }

    @Operation(summary = "查询我的提审 [ADMIN, OPERATOR]", description = "role限制：[ADMIN, OPERATOR]")
    @ApiPermissions({"ADMIN", "OPERATOR"})
    @GetMapping("/approval/my/create")
    public JsonResult<List<Approval>> getByCreator() {
        return new JsonResult<>(OK, approvalService.getByCreator());
    }

    @Operation(summary = "审批通过 [ADMIN]", description = "role限制：[ADMIN]")
    @ApiPermissions({"ADMIN"})
    @PostMapping("/approval/approve")
    public JsonResult<Approval> approve(@RequestBody ApproveQo approveQo) {
        return new JsonResult<>(OK, approvalService.approval(approveQo.getId(), approveQo.getRemark()));
    }

    @Operation(summary = "审批拒绝 [ADMIN]", description = "role限制：[ADMIN]")
    @ApiPermissions({"ADMIN"})
    @PostMapping("/approval/reject")
    public JsonResult<Approval> reject(@RequestBody ApproveQo approveQo) {
        return new JsonResult<>(OK, approvalService.reject(approveQo.getId(), approveQo.getRemark()));
    }
}
