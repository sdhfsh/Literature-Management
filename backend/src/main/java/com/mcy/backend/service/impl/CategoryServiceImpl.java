package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.Category;
import com.mcy.backend.service.CategoryService;
import com.mcy.backend.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【category】的数据库操作Service实现
* @createDate 2025-04-29 17:42:43
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




