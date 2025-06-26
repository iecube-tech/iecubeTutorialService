package com.iecube.iecubetutorial.model.user.mapper;

import com.iecube.iecubetutorial.model.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByAccount(String account);

    int addUser(User user);
}
