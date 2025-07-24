package com.iecube.iecubetutorial.model.resource.service.impl;

import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.exception.*;
import com.iecube.iecubetutorial.model.resource.mapper.ResourceMapper;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Value("${html.output.directory}")
    private String outputDirectory;

    @Autowired
    private ResourceMapper resourceMapper;

    // 上传文件的最大值
    private static final int FILE_MAX_SIZE= 1024 * 1024 * 1024;

    // 支持的文件类型
    private static final List<String> FILE_TYPE=new ArrayList<>();
    static {
        FILE_TYPE.add("image/jpeg");
        FILE_TYPE.add("image/png");
        FILE_TYPE.add("image/bmp");
        FILE_TYPE.add("image/gif");
        FILE_TYPE.add("text/html");
    }

    @Override
    public Resource writeHtmlToFile(String htmlContentBase64) {
        log.info("开始写文件");
        // 生成唯一文件名 (格式: timestamp_uuid.html)
        String timestamp = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = timestamp + "_" + uuid + ".html";
        // 确保输出目录存在
        try{
            Path directory = Paths.get(outputDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // 构建完整文件路径
            Path filePath = directory.resolve(fileName.endsWith(".html") ? fileName : fileName + ".html");
            // Base64解码
            byte[] decodedBytes = Base64.getDecoder().decode(htmlContentBase64);
            String htmlContent = new String(decodedBytes, StandardCharsets.UTF_8);

            // 写入HTML内容（确保覆盖原有内容）
            Files.writeString(filePath, htmlContent,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            // 计算文件MD5
            String md5 =calculateFileMD5(filePath);

            Resource resource = new Resource();
            resource.setFilename(fileName);
            resource.setType("text/html");
            resource.setMd5(md5);
            resource.setCreateTime(new Date());
            log.info("文件处理成功：{}", fileName);
            return resource;
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("处理文件失败：{},{}",fileName,e.getMessage());
            throw new HandelFileFailedException(e);
        }
    }

    @Override
    public Resource saveResource(Resource resource) {
        int res = resourceMapper.addResource(resource);
        if(res!=1){
            throw new InsertException("服务错误，新增数据异常");
        }
        return resource;
    }

    @Override
    public void deleteResource(Long resourceId) {
        Resource resource = resourceMapper.getResource(resourceId);
        if(resource==null){
            return;
        }
        String fileName = resource.getFilename();
        // 构建完整文件路径
        try{
            if (delFile(fileName)){
                int res = resourceMapper.deleteResource(resourceId);
                if(res!=1){
                    throw new DeleteException("服务错误，删除数据异常");
                }
            }
        }catch (IOException e){
            throw new HandelFileFailedException(e.getMessage());
        }
    }

    @Override
    public Resource updateResource(String htmlContentBase64, Resource resource) {
        String fileName = resource.getFilename();
        try{
            Path directory = Paths.get(outputDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            // 构建完整文件路径
            Path filePath = directory.resolve(fileName.endsWith(".html") ? fileName : fileName + ".html");
            // Base64解码
            byte[] decodedBytes = Base64.getDecoder().decode(htmlContentBase64);
            String htmlContent = new String(decodedBytes, StandardCharsets.UTF_8);

            // 写入HTML内容（确保覆盖原有内容）
            Files.writeString(filePath, htmlContent,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            // 计算文件MD5
            String md5 =calculateFileMD5(filePath);

            resource.setFilename(fileName);
            resource.setType("text/html");
            resource.setMd5(md5);
            resource.setUpdateTime(new Date());
            int re = resourceMapper.updateResource(resource);
            if(re!=1){
                throw new UpdateException("服务错误，更新数据异常");
            }
            return resource;
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("处理文件失败：{},{}",fileName,e.getMessage());
            throw new HandelFileFailedException(e);
        }
    }

    @Override
    public Resource uploadFile(MultipartFile file) {
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>FILE_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        if(!FILE_TYPE.contains(file.getContentType())){
            throw new FileTypeException("不支持的文件格式");
        }
        String fileName = this.saveFile(file);
        Resource resource = new Resource();
        resource.setFilename(fileName);
        resource.setType(file.getContentType());
        Path filePath = Paths.get(outputDirectory, fileName);
        try {
            resource.setMd5(this.calculateFileMD5(filePath));
        } catch (Exception e) {
            throw new HandelFileFailedException(e);
        }
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        return this.saveResource(resource);
    }

    @Override
    public Resource getResourceById(Long resourceId) {
        return resourceMapper.getResource(resourceId);
    }

    private String saveFile(MultipartFile file) {
        String timestamp = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String fileName = timestamp + "_" + uuid + "."+suffix;
        File targetFile = new File(outputDirectory, fileName);
        try{
            if (!targetFile.exists()) {
                File parentFile = targetFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                boolean success = targetFile.createNewFile();
                if (!success){
                    throw new WriteFileException("创建文件失败");
                }
            }
            FileCopyUtils.copy(file.getBytes(), targetFile);
        } catch (IOException e) {
            throw new WriteFileException(e);
        }
        return fileName;
    }

    private boolean delFile(String fileName) throws IOException {
        Path filePath = Paths.get(outputDirectory, fileName);
        // 验证文件存在且是普通文件
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return false;
        }
        // 验证文件是否在配置的目录内，防止目录遍历攻击
        Path canonicalPath = filePath.toRealPath();
        Path canonicalDir = Paths.get(outputDirectory).toRealPath();

        if (!canonicalPath.startsWith(canonicalDir)) {
            throw new IOException("拒绝访问: 文件不在配置的目录内");
        }
        // 执行删除操作
        Files.delete(filePath);
        return true;
    }

    private String calculateFileMD5(Path filePath) throws IOException, NoSuchAlgorithmException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(fileBytes);
        // 转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
