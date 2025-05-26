package com.iecube.iecubetutorial.model.user.service;

import com.iecube.iecubetutorial.model.user.dto.LoginQo;
import com.iecube.iecubetutorial.model.user.dto.RegisterQo;
import com.iecube.iecubetutorial.model.user.vo.LoginVo;
import com.iecube.iecubetutorial.model.user.vo.UserImportVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    boolean phoneExists(String phone);

    void register(RegisterQo registerQo);

    LoginVo jwtLogin(LoginQo loginQo, StringRedisTemplate stringRedisTemplate);

    UserImportVo processExcel(MultipartFile file);
}
