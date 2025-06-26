package com.iecube.iecubetutorial.model_user.account.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class Account extends BaseEntity {
    private Long id;
    private Long oSecId;
    private String phone;
    private String role;
    private String status;
}
