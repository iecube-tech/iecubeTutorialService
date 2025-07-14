package com.iecube.iecubetutorial.model.tags.service;

import com.iecube.iecubetutorial.model.tags.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();

    List<Tag> addTag(Tag tag);

    List<Tag> deleteTag(Tag tag);
}
