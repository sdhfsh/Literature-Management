package com.mcy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mcy.backend.entity.Menu;
import com.mcy.backend.entity.Result;
import com.mcy.backend.entity.User;
import com.mcy.backend.entity.UserRole;
import com.mcy.backend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统菜单控制器
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 查询菜单树信息
     *
     * @return
     */
    @RequestMapping("/treeList")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result treeList() {
        List<Menu> menuList = menuService.list(new QueryWrapper<Menu>().orderByAsc("order_num"));
        return Result.ok().put("treeMenu", menuService.buildTreeMenu(menuList));
    }

    /**
     * 修改功能，需要先根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        Menu menu = menuService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("menu", menu);
        return Result.ok(map);
    }

    /**
     * 添加或者修改用户
     *
     * @param menu
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:menu:edit')" + "||" + "hasAuthority('system:menu:add')")
    public Result save(@RequestBody Menu menu) {
        System.out.println();
        if (menu.getId() == null || menu.getId() == -1) {
            // 如果用户id不存在，则为新增用户
            menu.setCreateTime(new Date());
            menuService.save(menu);
        } else {
            // 如果id存在，则为修改用户信息
            menu.setUpdateTime(new Date());
            menuService.updateById(menu);
        }
        return Result.ok("保存成功");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result delete(@PathVariable(value = "id") Integer id) {
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if (count > 0) {
            return Result.error("不能直接删除目录，请先删除子菜单！");
        }
        menuService.removeById(id);
        return Result.ok();
    }
}
