package com.iecube.iecubetutorial.model_admin.user.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.model_admin.user.qo.ALoginQo;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import com.iecube.iecubetutorial.token.TokenDto;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "管理端登录认证")
public class SmAuthController extends BaseController {

    @Autowired
    private AUserService userService;

    @Operation(summary = "发送验证码")
    @GetMapping("/login")
    private JsonResult<Void> loginCode(String phone) {
        if(phone==null||phone.isEmpty()){
            throw new AuthException("请求参数错误");
        }
        String regex = "^1[3-9]\\d{9}$";
        if(!phone.matches(regex)){
            throw new AuthException("请输入正确的手机号");
        }
        userService.sendVCode(phone);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    private JsonResult<TokenDto> login(@RequestBody ALoginQo ALoginQo) {
         return new JsonResult<>(OK, userService.Login(ALoginQo));
    }

    @Operation(summary = "token刷新", requestBody=@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "刷新token请求体",
                            summary = "refreshToken键值对",
                            description = "携带refreshToken的键值对",
                            value ="{" +
                                    "\"refreshToken\": \"token\"" +
                                    "}"
                    )
            })
    ))
    @PostMapping("/refresh" )
    public JsonResult<TokenDto> refreshToken(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthException("刷新令牌不能为空");
        }
        TokenDto TokenDto = userService.refreshToken(refreshToken);
        return new JsonResult<>(OK, TokenDto);
    }
}
