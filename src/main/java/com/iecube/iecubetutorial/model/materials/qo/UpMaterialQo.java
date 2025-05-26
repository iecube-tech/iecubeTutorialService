package com.iecube.iecubetutorial.model.materials.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(title="更新请求对象")
@Data
public class UpMaterialQo {
    @Schema(description = "MaterialId")
    private Long id;
    @Schema(description = "Base64加密的html内容")
    private String htmlContentBase64;
}
