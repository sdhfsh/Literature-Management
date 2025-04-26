package com.mcy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcy.backend.entity.*;
import com.mcy.backend.service.RoleMenuService;
import com.mcy.backend.service.RoleService;
import com.mcy.backend.service.UserRoleService;
import com.mcy.backend.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 系统角色Controller控制器
     */
    @GetMapping("/listAll")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result listAll() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Role> roleList = roleService.list();
        resultMap.put("roleList", roleList);
        return Result.ok(resultMap);
    }

    /**
     * 根据条件，分页查询角色信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<Role> pageResult = roleService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()), new QueryWrapper<Role>().like(StringUtil.isNotEmpty(query), "name", query));
        List<Role> roleList = pageResult.getRecords();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("roleList", roleList);
        resultMap.put("total", pageResult.getTotal());
        return Result.ok(resultMap);
    }

    /**
     * 添加或者修改角色
     *
     * @param role
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:role:edit')" + "||" + "hasAuthority('system:role:add')")
    public Result save(@RequestBody Role role) {
        if (role.getId() == null || role.getId() == -1) {
            // 如果用户id不存在，则为新增用户
            role.setId(null);
            role.setCreateTime(new Date());
            roleService.save(role);
        } else {
            // 如果id存在，则为修改用户信息
            role.setUpdateTime(new Date());
            roleService.updateById(role);
        }
        return Result.ok("保存成功");
    }

    /**
     * 修改功能，需要先根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        Role role = roleService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("role", role);
        return Result.ok(map);
    }

    /**
     * 删除 / 批量删除
     *
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result delete(@RequestBody Integer[] ids) {
        roleService.removeByIds(Arrays.asList(ids));
        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id", ids));
        return Result.ok();
    }

    /**
     * 获取当前角色的权限菜单id集合
     * @param id
     * @return
     */
    @GetMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public Result menus(@PathVariable(value = "id") Integer id) {
        List<RoleMenu> roleMenuList = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));
        List<Integer> menuIdList = roleMenuList.stream().map(menu -> menu.getMenuId()).collect(Collectors.toList());
        return Result.ok().put("menuIdList", menuIdList);
    }

    /**
     * 更新角色权限信息
     * @param id
     * @param menuIds
     * @return
     */
    @Transactional
    @PostMapping("/updateMenus/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result updateMenus(@PathVariable(value = "id") Integer id, @RequestBody Integer[] menuIds) {
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id", id));
        List<RoleMenu> roleMenuList = new ArrayList<>();
        Arrays.asList(menuIds).forEach(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setId(null);
            roleMenu.setRoleId(id);
            roleMenu.setMenuId(menuId);
            roleMenuList.add(roleMenu);
        });

        roleMenuService.saveBatch(roleMenuList);
        return Result.ok();
    }
}
