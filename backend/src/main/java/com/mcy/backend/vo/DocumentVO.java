package com.mcy.backend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcy.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVO {

    /**
     * -1 表示新增，其他表示编辑
     */
    private Integer id;

    /**
     * 文献标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 摘要
     */
    private String excerpt;

    /**
     * 期刊名称
     */
    private String journal;

    /**
     * 分类
     */
    private List<String> category;

    /**
     * 发表日期
     */
    @JsonProperty("publication_data")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publicationDate;

    /**
     * 出版机构
     */
    private String publisher;

    /**
     * 卷号
     */
    private String volume;

    /**
     * 期号
     */
    private String issue;

    /**
     * 页码范围
     */
    private String pages;

    /**
     * DOI（可选）
     */
    private String doi;

    /**
     * URL（可选）
     */
    private String url;

    /**
     * 文献类型
     */
    private String type;

    /**
     * 是否共享
     */
    private String status;

    /**
     * 存储文件路径
     */
    @JsonProperty("file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @JsonProperty("file_type")
    private String fileType;

    /**
     * 上传时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 上传者
     */
    private User user;
}
