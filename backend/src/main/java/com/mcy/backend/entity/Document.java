package com.mcy.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName document
 */
@TableName(value ="document")
@Data
public class Document implements Serializable {
    /**
     * 文献ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文献标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 作者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 摘要
     */
    @TableField(value = "excerpt")
    private String excerpt;

    /**
     * 期刊名称
     */
    @TableField(value = "journal")
    private String journal;

    /**
     * 发表日期
     */
    @TableField(value = "publication_date")
    private Date publicationDate;

    /**
     * 出版机构
     */
    @TableField(value = "publisher")
    private String publisher;

    /**
     * 卷号
     */
    @TableField(value = "volume")
    private String volume;

    /**
     * 期号
     */
    @TableField(value = "issue")
    private String issue;

    /**
     * 页码范围
     */
    @TableField(value = "pages")
    private String pages;

    /**
     * DOI（可选）
     */
    @TableField(value = "doi")
    private String doi;

    /**
     * URL（可选）
     */
    @TableField(value = "url")
    private String url;

    /**
     * 文献类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 存储文件路径
     */
    @TableField(value = "file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @TableField(value = "file_type")
    private String fileType;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private Date updatedAt;

    /**
     * 上传用户ID
     */
    @TableField(value = "owner_id")
    private Integer ownerId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}