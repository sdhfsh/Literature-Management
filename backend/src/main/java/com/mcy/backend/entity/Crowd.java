package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName crowd
 */
@TableName(value ="crowd")
@Data
public class Crowd implements Serializable {
    /**
     * 组ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 小组名称
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 小组描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 创建者ID
     */
    @TableField(value = "created_by")
    private Integer createdBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    /**
     * 小组成员数量
     */
    @TableField(exist = false)
    private Integer memberNum;

    /**
     * 每个小组对应的申请消息
     */
    @TableField(exist = false)
    private List<CrowdApplication> crowdApplications;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}