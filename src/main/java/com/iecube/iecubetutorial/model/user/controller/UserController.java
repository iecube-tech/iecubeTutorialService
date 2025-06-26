package com.iecube.iecubetutorial.model.user.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.service.UserService;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import com.iecube.iecubetutorial.util.JsonResult;
import com.iecube.iecubetutorial.util.jwt.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account/")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public JsonResult<LoginVo> login(@RequestBody LoginQo loginQo) {
        if(loginQo.getAccount() == null){
            handleParamsError("account");
        }
        if(loginQo.getPassword() == null){
            handleParamsError("password");
        }
        LoginVo loginVo = userService.jwtLogin(loginQo, stringRedisTemplate);
        log.info("用户登录：{}_{}", loginVo.getUser().getId(), loginQo.getAccount());
        return new JsonResult<>(OK, loginVo);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public JsonResult<Void> logout() {
        AuthUtils.rm(stringRedisTemplate);
        return new JsonResult<>(OK);
    }
}
