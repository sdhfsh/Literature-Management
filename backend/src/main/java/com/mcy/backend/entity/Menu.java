package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @TableName menu
 */
@TableName(value ="menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "icon")
    private String icon;

    @TableField(value = "parent_id")
    private Integer parentId;

    @TableField(value = "order_num")
    private Integer orderNum;

    @TableField(value = "path")
    private String path;

    @TableField(value = "component")
    private String component;

    @TableField(value = "menu_type")
    private String menuType;

    @TableField(value = "perms")
    private String perms;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "remark")
    private String remark;

    private static final long serialVersionUID = 1L;

    // 不能映射数据库
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();
}