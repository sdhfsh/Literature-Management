package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.UserCrowd;
import com.mcy.backend.service.UserCrowdService;
import com.mcy.backend.mapper.UserCrowdMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【user_crowd(用户小组关系表)】的数据库操作Service实现
* @createDate 2025-05-07 15:10:33
*/
@Service
public class UserCrowdServiceImpl extends ServiceImpl<UserCrowdMapper, UserCrowd>
    implements UserCrowdService{

}




