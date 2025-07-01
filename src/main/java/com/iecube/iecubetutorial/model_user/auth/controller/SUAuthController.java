package com.iecube.iecubetutorial.model_user.auth.controller;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.model_admin.user.qo.ALoginQo;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.auth.dto.AuthDto;
import com.iecube.iecubetutorial.model_user.auth.service.SUAuthService;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/su/auth/")
@Tag(name = "用户端登录认证")
public class SUAuthController extends BaseController {

    @Autowired
    private SUAuthService authService;

    @Operation(summary = "用户端发送验证码")
    @GetMapping("/login")
    public JsonResult<Void> sendCode(String phone){
        if(phone==null||phone.isEmpty()){
            throw new AuthException("请求参数错误");
        }
        String regex = "^1[3-9]\\d{9}$";
        if(!phone.matches(regex)){
            throw new AuthException("请输入正确的手机号");
        }
        authService.sendCode(phone);
        return new JsonResult<>(OK);
    }


    @Operation(summary = "用户端登录")
    @PostMapping("/login")
    public JsonResult<AuthDto> login(@RequestBody ALoginQo aLoginQo){
        if(aLoginQo==null || aLoginQo.getPhone() == null || aLoginQo.getCode() == null){
            throw new AuthException("请求参数错误");
        }
        AuthDto res = authService.login(aLoginQo.getPhone(),aLoginQo.getCode());
        return new JsonResult<>(OK, res);
    }

    @Operation(summary = "用户端选择组织登录/切换组织 [USER_M, USER]", description = "当用户在多个组织中存在角色时， /login 返回 login=false, 以及用户所在的组织列表， 此时需要用户选择登录哪一个组织下的账户。")
    @PostMapping("/relogin")
    public JsonResult<AuthDto> reLogin(Long orgSecId){
        if(orgSecId==null){
            throw new AuthException("请求参数错误");
        }
        AuthDto res = authService.reLogin(orgSecId);
        return new JsonResult<>(OK, res);
    }

    @Operation(summary = "用户端用户获取所在组织[USER_M, USER]")
    @ApiPermissions({"USER_M","USER"})
    @GetMapping("/orgs")
    public JsonResult<List<OrgSec>> getOrgs(){
        return new JsonResult<>(OK, authService.accountOrgSecList());
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
    public JsonResult<AuthDto> reFresh(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthException("刷新令牌不能为空");
        }
        AuthDto authDto = authService.refreshToken(refreshToken);
        return new JsonResult<>(OK, authDto);
    }


    @GetMapping("/colleague")
    @Operation(summary = "用户端用户获取所在组织人员列表 [USER_M, USER]")
    @ApiPermissions({"USER_M","USER"})
    public JsonResult<List<AccountVo>> getOrgSecUserList(){
        return new JsonResult<>(OK, authService.getUserListByAccount());
    }

}
