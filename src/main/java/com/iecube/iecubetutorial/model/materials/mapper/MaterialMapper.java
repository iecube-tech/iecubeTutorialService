package com.iecube.iecubetutorial.model.materials.mapper;

import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialMapper {
    int addMaterial(MaterialEntity material);

    int updateMaterial(MaterialEntity material);

    int deleteMaterial(Long id);

    List<MaterialEntity> getMaterials(Long userId);

    MaterialEntity getMaterial(Long id);

    MaterialEntity getMaterialByChatId(String chatId);
}
