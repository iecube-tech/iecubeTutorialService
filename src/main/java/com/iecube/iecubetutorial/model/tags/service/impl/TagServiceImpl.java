package com.iecube.iecubetutorial.model.tags.service.impl;

import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import com.iecube.iecubetutorial.model.tags.mapper.TagMapper;
import com.iecube.iecubetutorial.model.tags.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.getAll();
    }

    @Override
    public List<Tag> addTag(Tag tag) {
        int res = tagMapper.insertTag(tag);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return this.getAllTags();
    }

    @Override
    public List<Tag> deleteTag(Tag tag) {
        int res = tagMapper.deleteTag(tag);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return this.getAllTags();
    }

}
