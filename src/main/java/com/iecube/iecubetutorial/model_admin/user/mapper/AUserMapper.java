package com.iecube.iecubetutorial.model_admin.user.mapper;

import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import org.apache.ibatis.annotations.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper
public interface AUserMapper {

    int addUser(AUser user);

    AUser getUserByPhone(String phone);

    List<AUser> getAllUsers();

    List<AUser> getAdminUsers();

    int updateUser(AUser user);

    int deleteUser(String phone, String lastOperator, Instant lastOperateTime);
}
