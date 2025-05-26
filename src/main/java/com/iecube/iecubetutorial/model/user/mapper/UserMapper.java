package com.iecube.iecubetutorial.model.user.mapper;

import com.iecube.iecubetutorial.model.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByPhone(String phone);

    int addUser(User user);
}
