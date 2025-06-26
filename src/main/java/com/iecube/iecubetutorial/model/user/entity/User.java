package com.iecube.iecubetutorial.model.user.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String account;
    private String password;
    private String salt;
    private String name;
}
