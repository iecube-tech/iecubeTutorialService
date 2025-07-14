package com.iecube.iecubetutorial.model.s_m_t.mapper;

import com.iecube.iecubetutorial.model.s_m_t.entity.SMaterialTag;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SMaterialTagMapper {
    int insert(SMaterialTag record);
    int batchInsert(List<SMaterialTag> list);
    int delete(SMaterialTag record);

    List<Tag> getTagsBySMaterialId(Long sMaterialId);
}
