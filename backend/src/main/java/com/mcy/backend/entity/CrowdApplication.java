package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 小组申请表
 * @TableName crowd_application
 */
@TableName(value ="crowd_application")
@Data
public class CrowdApplication implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 申请者id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 小组Id
     */
    @TableField(value = "crowd_id")
    private Integer crowdId;

    /**
     * 申请状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 申请时间
     */
    @TableField(value = "apply_time")
    private Date applyTime;

    /**
     * 审核时间
     */
    @TableField(value = "decision_time")
    private Date decisionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}