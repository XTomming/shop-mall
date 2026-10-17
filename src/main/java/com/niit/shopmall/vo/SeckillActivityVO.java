package com.niit.shopmall.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀场次视图（含商品列表）
 */
@Data
public class SeckillActivityVO {

    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private List<SeckillItemVO> items;
}
