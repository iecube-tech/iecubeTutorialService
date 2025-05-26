package com.iecube.iecubetutorial.model.user.dto;

import lombok.Data;

@Data
public class UserImportQo {
    private String name;
    private String school;
    private String collage;
    private String phone;
    private String email;
    private UserImportRes res;
}
