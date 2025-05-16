package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.DocumentStatus;
import com.mcy.backend.service.DocumentStatusService;
import com.mcy.backend.mapper.DocumentStatusMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【document_status】的数据库操作Service实现
* @createDate 2025-05-04 22:34:38
*/
@Service
public class DocumentStatusServiceImpl extends ServiceImpl<DocumentStatusMapper, DocumentStatus>
    implements DocumentStatusService{

}




