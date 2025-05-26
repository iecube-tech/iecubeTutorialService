package com.iecube.iecubetutorial.model.materials.qo;

import lombok.Data;

/**
 * name 文件名
 * title 课程名
 * knowledgePoints 知识点
 * instruction 指令
 */
@Data
public class MaterialQo {
    private String name;
    private String title;
    private String knowledgePoints;
    private String instruction;
}
