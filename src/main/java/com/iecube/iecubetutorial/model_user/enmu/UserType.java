package com.iecube.iecubetutorial.model_user.enmu;

import lombok.Getter;

@Getter
public enum UserType {
    ORGANIZATION("组织"),
    INDIVIDUAL("个人");

    private final String label;

    UserType(String label) {
        this.label = label;
    }
}
