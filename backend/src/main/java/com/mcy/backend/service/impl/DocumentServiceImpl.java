package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.Document;
import com.mcy.backend.service.DocumentService;
import com.mcy.backend.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【document】的数据库操作Service实现
* @createDate 2025-05-04 21:26:19
*/
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService{

}




