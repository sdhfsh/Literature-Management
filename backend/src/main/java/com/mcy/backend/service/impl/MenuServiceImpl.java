package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.Menu;
import com.mcy.backend.service.MenuService;
import com.mcy.backend.mapper.MenuMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【menu】的数据库操作Service实现
* @createDate 2025-03-21 15:12:38
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

}




