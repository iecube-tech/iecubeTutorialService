package com.iecube.iecubetutorial.model_admin.user.enmu;

import lombok.Getter;

@Getter
public enum AUserRole {
    SUPER("SUPER","超级管理员"), ADMIN("ADMIN","管理员"), USER("USER","运营");

    private final String role;
    private final String label;

    AUserRole(String role, String label) {
        this.role = role;
        this.label = label;
    }
}
