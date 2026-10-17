package com.niit.shopmall.controller.admin;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.util.FileUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台文件上传接口
 */
@Api(tags = "后台-文件上传")
@RestController
@RequestMapping("/api/admin")
public class UploadController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileUploadUtil.uploadImage(file));
    }
}
