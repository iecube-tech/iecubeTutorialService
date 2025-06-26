package com.iecube.iecubetutorial.model_admin.operator.vo;

import lombok.Data;

@Data
public class userTypeVo {
    private String value;
    private String label;

    public userTypeVo(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
