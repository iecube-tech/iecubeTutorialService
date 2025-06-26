package com.iecube.iecubetutorial.model_admin.user.service;

import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_admin.user.qo.ALoginQo;
import com.iecube.iecubetutorial.model_admin.user.qo.AUserQo;
import com.iecube.iecubetutorial.token.TokenDto;

import java.util.List;

public interface AUserService {

    void sendVCode(String phone);

    TokenDto Login(ALoginQo ALoginQo);

    TokenDto refreshToken(String refreshToken);

    AUser CreateUser(AUserQo aUserQo, String operator);

    AUser UpdateUser(AUserQo aUserQo, String operator );

    AUser getUserByPhone(String phone);

    List<AUser> deleteUser(AUserQo aUserQo, String operator);

    AUser disableUser(AUserQo aUserQo, String operator);

    AUser enableUser(AUserQo aUserQo, String operator);

    List<AUser> GetAllUsers();

    List<AUser> GetAdminUsers();
}
