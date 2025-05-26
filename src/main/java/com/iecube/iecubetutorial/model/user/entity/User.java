package com.iecube.iecubetutorial.model.user.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String role;
    private String name;
    private String phone;
    private String school;
    private String collage;
    private Date createTime;
}
