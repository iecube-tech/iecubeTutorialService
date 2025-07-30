package com.iecube.iecubetutorial.model.resource.controller;

import com.iecube.iecubetutorial.baseController.BaseController;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.util.DownloadUtil;
import com.iecube.iecubetutorial.util.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/file")
@Tag(name="文件")
public class ResourceController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    // 文件路径
    @Value("${resource-location}")
    private String files;

    @PostMapping
    @Operation(summary = "上传文件")
    public JsonResult<Resource> uploadFile(MultipartFile file) {
        Resource resource = resourceService.uploadFile(file);
        return new JsonResult<>(OK, resource);
    }


    /**
     * 请求文件
     * @param fileName 文件名
     * @param response HttpServletResponse
     */
    @GetMapping("/resource/{fileName}")
    public void GetFile(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
        DownloadUtil.httpDownload(new File(this.files, fileName), resource.getFilename(), response);
    }
}
