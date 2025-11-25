package com.lyx.secondhandsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 分类实体类
@Data
@TableName("categories")
public class Category {
    /** 分类主键ID（自增） */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    /** 分类名称（唯一） */
    private String name;

    /** 展示排序（越大越靠前） */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
