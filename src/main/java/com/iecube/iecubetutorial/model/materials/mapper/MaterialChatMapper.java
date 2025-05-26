package com.iecube.iecubetutorial.model.materials.mapper;

import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialChatMapper {
    int addMaterialChat(MaterialChat materialChat);
}
