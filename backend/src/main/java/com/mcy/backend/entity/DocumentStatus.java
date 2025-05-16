package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName document_status
 */
@TableName(value ="document_status")
@Data
public class DocumentStatus implements Serializable {
    /**
     * 状态id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文献id
     */
    @TableField(value = "document_id")
    private Integer documentId;

    /**
     * 状态（private  public）
     */
    @TableField(value = "status")
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}