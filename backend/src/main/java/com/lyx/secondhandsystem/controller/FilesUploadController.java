package com.lyx.secondhandsystem.controller;

import com.lyx.secondhandsystem.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// 文件上传控制器
@RestController
@RequestMapping("/api/files")
public class FilesUploadController {
    // 上传文件
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // 上传文件接口
    public ApiResponse<String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) throw new IllegalArgumentException("文件不能为空");
        String filename = StringUtils.cleanPath(file.getOriginalFilename()); // 清理文件名中的路径分隔符
        // 获取文件扩展名
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase() : ""; 
        // 检查文件扩展名是否合法
        if (!(ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("webp"))) {
            throw new IllegalArgumentException("仅支持png/jpg/jpeg/webp");
        }
        // 检查文件大小是否合法
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("文件大小不得超过5MB");
        // 检查文件目录是否存在，不存在则创建
        //Path dir = Paths.get("uploads");
        Path dir = Paths.get("uploads").toAbsolutePath();
        if (!Files.exists(dir)) Files.createDirectories(dir);
        // 生成新文件名
        String newName = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
        
        // 检查目标文件是否已存在，存在则删除
        Path target = dir.resolve(newName);
        //file.transferTo(target.toFile()); // 上传文件到目标路径
        //String url = "/uploads/" + newName; // 构建文件访问URL
        //return ApiResponse.ok(url); // 返回文件访问URL
        Files.copy(file.getInputStream(), target);
        String url = "/uploads/" + newName;
        return ApiResponse.ok(url);
    }
}
