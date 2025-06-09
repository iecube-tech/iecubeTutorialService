package com.iecube.iecubetutorial.model_admin.user.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AUser extends BaseEntity {
    private String phone;
    private String name;
    private String status;
    private String role;
}
