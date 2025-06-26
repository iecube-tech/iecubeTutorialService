package com.iecube.iecubetutorial.model_user.account.vo;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class AccountVo extends BaseEntity {
    private long id;
    private Long oSecId;
    private String oSecName;
    private String oSecType;
    private String phone;
    private String name;
    private String email;
    private String role;
    private String status;
}
