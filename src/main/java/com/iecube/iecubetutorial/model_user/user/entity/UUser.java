package com.iecube.iecubetutorial.model_user.user.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class UUser extends BaseEntity {
    private String phone;
    private String name;
    private String email;
    private String status;
}
