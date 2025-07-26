package com.iecube.iecubetutorial.model.resource.service;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ResourceService {

    Resource writeHtmlToFile(String htmlContentBase64);

    Resource saveResource(Resource resource);

    Resource copyResource(Long resourceId) throws IOException, NoSuchAlgorithmException;

    void deleteResource(Long resourceId);

    Resource updateResource(String htmlContentBase64, Resource resource);

    Resource uploadFile(MultipartFile file);

    Resource getResourceById(Long resourceId);

    Resource getResourceByFilename(String filename);
}
