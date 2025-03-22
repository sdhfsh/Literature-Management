package com.mcy.backend.service;

import com.mcy.backend.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 30679
* @description 针对表【menu】的数据库操作Service
* @createDate 2025-03-21 15:12:38
*/
public interface MenuService extends IService<Menu> {

    List<Menu> buildTreeMenu(List<Menu> menuList);
}
