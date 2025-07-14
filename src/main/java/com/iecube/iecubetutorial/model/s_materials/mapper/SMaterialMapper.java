package com.iecube.iecubetutorial.model.s_materials.mapper;

import com.iecube.iecubetutorial.model.s_materials.entity.SMaterial;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SMaterialMapper {
    int Insert(SMaterial material);

    int deleteByPrimaryKey(Long id);

    SMaterial selectByPrimaryKey(Long id);

    List<SMaterial> selectAll();

    List<SMaterial> selectByKeyword(String keyword);

    List<SMaterial> selectByTag(Long tagId);
}
