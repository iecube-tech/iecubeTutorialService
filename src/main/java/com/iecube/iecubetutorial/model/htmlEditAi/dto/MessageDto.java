package com.iecube.iecubetutorial.model.htmlEditAi.dto;

import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import lombok.Data;

import java.util.List;

@Data
public class MessageDto {
    private String type;
    private String projectId;
    private String message;
    private List<Element> selectedElements;
    private String fullCode;  // html文本经过base64编码的文本
    private String fileName;
    private ProjectChildVo projectChildVo;
}
