package com.niit.shopmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价表
 */
@Data
@TableName("product_comment")
public class ProductComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long memberId;

    private Long orderId;

    private String content;

    private Integer rating;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
