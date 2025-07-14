package com.iecube.iecubetutorial.model.tags.mapper;


import com.iecube.iecubetutorial.model.tags.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    int insertTag(Tag tag);

    int deleteTag(Tag tag);

    List<Tag> getAll();
}

