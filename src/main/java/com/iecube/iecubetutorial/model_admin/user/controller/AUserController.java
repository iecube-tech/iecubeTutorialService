package com.iecube.iecubetutorial.model_admin.user.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.model_admin.user.entity.AUserRole;
import com.iecube.iecubetutorial.model_admin.user.qo.ALoginQo;
import com.iecube.iecubetutorial.model_admin.user.service.AUserRoleService;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import com.iecube.iecubetutorial.token.TokenDto;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sm/user")
public class AUserController extends BaseController {

    @Autowired
    private AUserService userService;

    @Autowired
    private AUserRoleService userRoleService;


    @Operation(summary = "发送验证码")
    @GetMapping("/login")
    private JsonResult<Void> loginCode(String phone) {
        userService.sendVCode(phone);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    private JsonResult<TokenDto> login(@RequestBody ALoginQo loginQo) {
         return new JsonResult<>(OK, userService.Login(loginQo));
    }

    @PostMapping("/refresh")
    public JsonResult<TokenDto> refreshToken(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthException("刷新令牌不能为空");
        }
        TokenDto TokenDto = userService.refreshToken(refreshToken);
        return new JsonResult<>(OK, TokenDto);
    }

    @Operation(summary = "查询用户角色列表")
    @GetMapping("/roles")
    public JsonResult<List<AUserRole>> getAllRole(){
        return new JsonResult<>(OK, userRoleService.roleList());
    }
}
