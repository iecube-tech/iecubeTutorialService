package com.iecube.iecubetutorial.model.mOutline.mapper;

import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MOutlineMapper {
    int insertOutline(MOutline outline);

    int updateOutline(MOutline outline);

    MOutline getOutlineById(int id);

    MOutline getOutlineByPId(String projectId);
}
