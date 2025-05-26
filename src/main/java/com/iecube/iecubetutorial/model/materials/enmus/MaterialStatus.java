package com.iecube.iecubetutorial.model.materials.enmus;

import lombok.Getter;


@Getter
public enum MaterialStatus {
    NOTReady("NOTReady", "未开始"),
    GENERATING("GENERATING", "生成中"),
    DONE("DONE", "完成"),
    FAILED("FAILED", "失败"),
    DEL("DEL", "已删除");

    private final String status;
    private final String description;

    MaterialStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public static MaterialStatus fromStatus(String sta) {
        for (MaterialStatus status : values()) {
            if (status.status.equals(sta)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的状态: " + sta);
    }
}