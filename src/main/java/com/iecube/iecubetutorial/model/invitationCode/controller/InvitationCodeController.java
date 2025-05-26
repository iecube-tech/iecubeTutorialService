package com.iecube.iecubetutorial.model.invitationCode.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.invitationCode.service.InvitationCodeService;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invitation/code")
public class InvitationCodeController extends BaseController {
    @Autowired
    private InvitationCodeService invitationCodeService;

    @Operation(summary = "申请邀请码")
    @PostMapping("/apply")
    public JsonResult<Void> applyCode(String email){
        invitationCodeService.applyCode(email);
        return new JsonResult<>(OK);
    }

}
