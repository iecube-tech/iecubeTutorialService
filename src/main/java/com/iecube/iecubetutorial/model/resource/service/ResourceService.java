package com.iecube.iecubetutorial.model.resource.service;

import com.iecube.iecubetutorial.model.resource.entity.Resource;

public interface ResourceService {

    Resource writeHtmlToFile(String htmlContentBase64);

    Resource saveResource(Resource resource);

    void deleteResource(Long resourceId);

    Resource updateResource(String htmlContentBase64, Resource resource);
}
