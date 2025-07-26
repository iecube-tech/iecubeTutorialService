package com.iecube.iecubetutorial.model.materials.service;

import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.qo.UpMaterialQo;
import com.iecube.iecubetutorial.model.materials.vo.MaterialVo;

import java.util.List;

public interface MaterialService {

    void genMaterialByOutline(String mOutlineId);

    void oneClickGen(MOutline mOutline, MaterialChat materialChat);

    List<MaterialVo> getMaterials(Long userId);

    MaterialEntity getMaterial(String chatId);

    MaterialEntity getMaterial(Long id);

    void handelUpload(MaterialEntity materialEntity);

    MaterialVo updateMaterial(UpMaterialQo upMaterialQo, Long userId);

    List<MaterialVo> deleteMaterial(Long id, Long userId);

    String decrypt(String encryptedText);

    List<MaterialVo> getAllMaterials();
}
