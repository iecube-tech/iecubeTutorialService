package com.iecube.iecubetutorial.model.user.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.dto.RegisterQo;
import com.iecube.iecubetutorial.model.user.service.UserService;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import com.iecube.iecubetutorial.model.user.vo.UserImportVo;
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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/account/")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public JsonResult<Void> register(@RequestBody RegisterQo registerQo) {
        if(registerQo.getPhone() == null){
            handleParamsError("phone");
        }
        if(registerQo.getCode() == null){
            handleParamsError("code");
        }
        userService.register(registerQo);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "手机号是否已注册")
    @PostMapping("/hasregister")
    public JsonResult<Boolean> isRegister(String phone) {
        return new JsonResult<>(OK, userService.phoneExists(phone));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public JsonResult<LoginVo> login(@RequestBody LoginQo loginQo) {
        if(loginQo.getPhone() == null){
            handleParamsError("phone");
        }
        if(loginQo.getVerifyCode() == null){
            handleParamsError("verifyCode");
        }
        if(loginQo.getInvitationCode() == null){
            handleParamsError("invitationCode");
        }
        LoginVo loginVo = userService.jwtLogin(loginQo, stringRedisTemplate);
        log.info("用户登录：{}_{}", loginVo.getUser().getId(), loginQo.getPhone());
        return new JsonResult<>(OK, loginVo);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public JsonResult<Void> logout() {
        AuthUtils.rm(stringRedisTemplate);
        return new JsonResult<>(OK);
    }

    @Operation(summary = "批量创建内测用户，单次最大数量限制100人")
    @PostMapping("/user/import")
    public JsonResult<UserImportVo> importExcel(@RequestBody MultipartFile file){
        UserImportVo vo =userService.processExcel(file);
        return new JsonResult<>(OK,vo);
    }
}
