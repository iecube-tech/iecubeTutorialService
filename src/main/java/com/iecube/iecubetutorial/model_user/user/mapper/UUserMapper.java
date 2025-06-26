package com.iecube.iecubetutorial.model_user.user.mapper;

import com.iecube.iecubetutorial.model_user.user.entity.UUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UUserMapper {
    int createUUser(UUser user);

    int createUUserBatch(List<UUser> list);

    UUser getUUserByPhone(String phone);
}
