package com.iecube.iecubetutorial.model.resource.mapper;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceMapper {

    int addResource(Resource resource);

    Resource getResource(Long id);

    int deleteResource(Long id);

    int updateResource(Resource resource);
}
