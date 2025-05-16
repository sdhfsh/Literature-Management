package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.CrowdApplication;
import com.mcy.backend.service.CrowdApplicationService;
import com.mcy.backend.mapper.CrowdApplicationMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【crowd_application(小组申请表)】的数据库操作Service实现
* @createDate 2025-05-07 16:43:43
*/
@Service
public class CrowdApplicationServiceImpl extends ServiceImpl<CrowdApplicationMapper, CrowdApplication>
    implements CrowdApplicationService{

}




