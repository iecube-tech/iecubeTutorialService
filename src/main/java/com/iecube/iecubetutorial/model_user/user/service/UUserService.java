package com.iecube.iecubetutorial.model_user.user.service;

import com.iecube.iecubetutorial.model_user.user.entity.UUser;

import java.util.List;

public interface UUserService {
    UUser createUser(UUser user);

    List<UUser> batchCreateUsers(List<UUser> users);

    UUser getUserByPhone(String phone);
}
