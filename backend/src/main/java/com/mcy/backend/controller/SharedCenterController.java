package com.mcy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcy.backend.entity.*;
import com.mcy.backend.service.DocCategoryService;
import com.mcy.backend.service.DocumentService;
import com.mcy.backend.service.DocumentStatusService;
import com.mcy.backend.service.UserService;
import com.mcy.backend.utils.StringUtil;
import com.mcy.backend.vo.DocumentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/center")
public class SharedCenterController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocCategoryService docCategoryService;

    @Autowired
    private DocumentStatusService documentStatusService;

    @Autowired
    private UserService userService;

    /**
     * 根据条件，分页查询用户信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<Document> pageResult = documentService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),
                new QueryWrapper<Document>()
                        .like(StringUtil.isNotEmpty(query), "title", query));
        List<Document> docList = pageResult.getRecords();
        System.out.println("docList: " + docList);

        List<DocumentVO> resDocList = new ArrayList<>();
        long total = pageResult.getTotal();
        for (Document document : docList) {
            // 获取文献的状态
            DocumentStatus documentStatus = documentStatusService.getOne(new LambdaQueryWrapper<DocumentStatus>().eq(DocumentStatus::getDocumentId, document.getId()));
            if ("public".equals(documentStatus.getStatus())) {
                // 获取文献标签
                List<DocCategory> docCategoryList = docCategoryService.list(new LambdaQueryWrapper<DocCategory>().eq(DocCategory::getDocumentId, document.getId()));
                List<String> categoryList = docCategoryList.stream().map(docCategory -> docCategory.getCategoryName()).collect(Collectors.toList());

                // 获取文献的上传者
                User ownUser = userService.getById(document.getOwnerId());

                DocumentVO documentVO = new DocumentVO();
                documentVO.setId(document.getId());
                documentVO.setTitle(document.getTitle());
                documentVO.setAuthor(document.getAuthor());
                documentVO.setExcerpt(document.getExcerpt());
                documentVO.setJournal(document.getJournal());
                documentVO.setCategory(categoryList);
                documentVO.setPublicationDate(document.getPublicationDate());
                documentVO.setPublisher(document.getPublisher());
                documentVO.setVolume(document.getVolume());
                documentVO.setIssue(document.getIssue());
                documentVO.setPages(document.getPages());
                documentVO.setDoi(document.getDoi());
                documentVO.setUrl(document.getUrl());
                documentVO.setType(document.getType());
                documentVO.setStatus(null);
                documentVO.setFilePath(document.getFilePath());
                documentVO.setFileType(document.getFileType());
                documentVO.setCreatedAt(document.getCreatedAt());
                documentVO.setUpdatedAt(document.getUpdatedAt());
                documentVO.setUser(ownUser);

                resDocList.add(documentVO);
            } else {
                total--;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("docList", resDocList);
        resultMap.put("total", total);
        System.out.println(resultMap);
        return Result.ok(resultMap);
    }
}
