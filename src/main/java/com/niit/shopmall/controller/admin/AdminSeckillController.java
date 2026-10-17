package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.SeckillActivityDTO;
import com.niit.shopmall.dto.SeckillItemBatchDTO;
import com.niit.shopmall.dto.SeckillItemDTO;
import com.niit.shopmall.entity.SeckillActivity;
import com.niit.shopmall.service.SeckillService;
import com.niit.shopmall.vo.SeckillItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 后台秒杀管理接口
 */
@Api(tags = "后台-秒杀管理")
@RestController
@RequestMapping("/api/admin/seckill")
public class AdminSeckillController {

    @Autowired
    private SeckillService seckillService;

    @ApiOperation("场次分页")
    @GetMapping("/activity/page")
    public Result<Page<SeckillActivity>> activityPage(@RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(seckillService.adminActivityPage(pageNum, pageSize));
    }

    @ApiOperation("新增场次")
    @PostMapping("/activity")
    public Result<Void> addActivity(@Valid @RequestBody SeckillActivityDTO dto) {
        seckillService.adminAddActivity(dto);
        return Result.success();
    }

    @ApiOperation("修改场次")
    @PutMapping("/activity/{id}")
    public Result<Void> updateActivity(@PathVariable Long id, @Valid @RequestBody SeckillActivityDTO dto) {
        seckillService.adminUpdateActivity(id, dto);
        return Result.success();
    }

    @ApiOperation("删除场次")
    @DeleteMapping("/activity/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        seckillService.adminDeleteActivity(id);
        return Result.success();
    }

    @ApiOperation("秒杀商品分页")
    @GetMapping("/item/page")
    public Result<Page<SeckillItemVO>> itemPage(@RequestParam(required = false) Long activityId,
                                                @RequestParam(defaultValue = "1") long pageNum,
                                                @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(seckillService.adminItemPage(activityId, pageNum, pageSize));
    }

    @ApiOperation("新增秒杀商品")
    @PostMapping("/item")
    public Result<Void> addItem(@Valid @RequestBody SeckillItemDTO dto) {
        seckillService.adminAddItem(dto);
        return Result.success();
    }

    @ApiOperation("批量新增秒杀商品")
    @PostMapping("/item/batch")
    public Result<Void> addItems(@Valid @RequestBody SeckillItemBatchDTO dto) {
        seckillService.adminAddItems(dto);
        return Result.success();
    }

    @ApiOperation("修改秒杀商品")
    @PutMapping("/item/{id}")
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody SeckillItemDTO dto) {
        seckillService.adminUpdateItem(id, dto);
        return Result.success();
    }

    @ApiOperation("删除秒杀商品")
    @DeleteMapping("/item/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        seckillService.adminDeleteItem(id);
        return Result.success();
    }
}
