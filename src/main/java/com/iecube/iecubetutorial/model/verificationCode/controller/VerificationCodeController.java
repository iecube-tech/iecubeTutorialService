package com.iecube.iecubetutorial.model.verificationCode.controller;


import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.verificationCode.qo.VCodeQo;
import com.iecube.iecubetutorial.model.verificationCode.servcie.SendVerificationCode;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms/code/")
public class VerificationCodeController extends BaseController {

    @Autowired
    private SendVerificationCode sendVerificationCode;

    @Operation(summary = "发送验证码")
    @PostMapping("/send")
    public JsonResult<Void> sendVerificationCode(@RequestBody VCodeQo vCodeQo) {
        sendVerificationCode.generateAndSendCode(vCodeQo.getPhone(), vCodeQo.getUsage());
        return new JsonResult<>(OK);
    }
}
