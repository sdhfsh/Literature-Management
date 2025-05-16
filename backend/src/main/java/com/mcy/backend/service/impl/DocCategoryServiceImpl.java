package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.DocCategory;
import com.mcy.backend.service.DocCategoryService;
import com.mcy.backend.mapper.DocCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【doc_category(文献标签分类)】的数据库操作Service实现
* @createDate 2025-04-29 18:18:03
*/
@Service
public class DocCategoryServiceImpl extends ServiceImpl<DocCategoryMapper, DocCategory>
    implements DocCategoryService{

}




