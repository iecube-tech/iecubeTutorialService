package com.iecube.iecubetutorial.model.s_materials.service;

import com.iecube.iecubetutorial.model.s_materials.qo.ExportQo;
import com.iecube.iecubetutorial.model.s_materials.qo.UploadQo;
import com.iecube.iecubetutorial.model.s_materials.vo.SMaterialVo;
import com.iecube.iecubetutorial.model.tags.entity.Tag;

import java.util.List;

public interface SMaterialService {
    // 通过讲义导入 另外需要 管理员可查看系统生成的所有讲义
    List<SMaterialVo> exportFromMaterial(ExportQo exportQo);

    // 上传
    List<SMaterialVo> uploadSMaterial(UploadQo uploadQo);

    List<SMaterialVo> deleteSMaterial(Long id);

    List<SMaterialVo> getAllMaterials();

    List<SMaterialVo> getMaterialsByKeyWords(String keyWords);

    List<SMaterialVo> getMaterialsByTag(Tag tag);
}
