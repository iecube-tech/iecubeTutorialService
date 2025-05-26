package com.iecube.iecubetutorial.model.user.vo;

import com.iecube.iecubetutorial.model.user.entity.User;
import lombok.Data;

@Data
public class LoginVo {
    private String token;
    private User user;
}
