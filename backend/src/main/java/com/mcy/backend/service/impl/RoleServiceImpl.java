package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.Role;
import com.mcy.backend.service.RoleService;
import com.mcy.backend.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【role】的数据库操作Service实现
* @createDate 2025-03-21 15:12:38
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




