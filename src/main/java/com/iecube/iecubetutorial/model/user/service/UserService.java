package com.iecube.iecubetutorial.model.user.service;

import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.entity.User;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import org.springframework.data.redis.core.StringRedisTemplate;

public interface UserService {

    LoginVo jwtLogin(LoginQo loginQo, StringRedisTemplate stringRedisTemplate);

    User addUser(String account, String password, String name);

}
