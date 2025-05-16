package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 文献标签分类
 * @TableName doc_category
 */
@TableName(value ="doc_category")
@Data
public class DocCategory implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文献id
     */
    @TableField(value = "document_id")
    private Integer documentId;

    /**
     * 标签名称
     */
    @TableField(value = "category_name")
    private String categoryName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}