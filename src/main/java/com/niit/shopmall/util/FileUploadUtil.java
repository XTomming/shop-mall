package com.niit.shopmall.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.niit.shopmall.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 本地文件上传工具
 */
@Component
public class FileUploadUtil {

    private static final List<String> ALLOWED_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

    @Value("${mall.upload.path}")
    private String uploadPath;

    /**
     * 上传图片，返回可访问的相对 URL
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = originalName == null ? "" : FileUtil.extName(originalName).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持图片格式：" + ALLOWED_EXT);
        }
        // 保存到本地目录，文件名用 UUID 避免冲突
        File dir = new File(uploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("上传目录创建失败");
        }
        String fileName = IdUtil.fastSimpleUUID() + "." + ext;
        try {
            File dest = new File(dir, fileName);
            file.transferTo(dest.getAbsoluteFile());
        } catch (Exception e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
        return "/upload/" + fileName;
    }
}
