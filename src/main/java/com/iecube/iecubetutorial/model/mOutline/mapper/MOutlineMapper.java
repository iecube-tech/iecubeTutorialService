package com.iecube.iecubetutorial.model.mOutline.mapper;

import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MOutlineMapper {
    int insertOutline(MOutline outline);

    int updateOutline(MOutline outline);

    MOutline getOutlineById(String id);

    MOutline getOutlineByPId(String projectId);

    MOutline getOutlineByChatId(String chatId);
}
