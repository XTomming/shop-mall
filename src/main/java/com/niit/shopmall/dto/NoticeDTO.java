package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 公告参数
 */
@Data
public class NoticeDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private Integer status;
}
