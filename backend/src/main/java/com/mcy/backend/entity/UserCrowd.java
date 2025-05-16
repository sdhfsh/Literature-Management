package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户小组关系表
 * @TableName user_crowd
 */
@TableName(value ="user_crowd")
@Data
@AllArgsConstructor
public class UserCrowd implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 小组id
     */
    @TableField(value = "crowd_id")
    private Integer crowdId;

    /**
     * 加入时间
     */
    @TableField(value = "join_time")
    private Date joinTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}