package com.mcy.backend.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcy.backend.common.exception.UserCountLockException;
import com.mcy.backend.entity.Menu;
import com.mcy.backend.entity.Role;
import com.mcy.backend.entity.User;
import com.mcy.backend.mapper.MenuMapper;
import com.mcy.backend.mapper.RoleMapper;
import com.mcy.backend.mapper.UserMapper;
import com.mcy.backend.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User loginUser = userMapper.selectOne(queryWrapper);
        System.out.println("loginUser: " + loginUser);
        if (Objects.isNull(loginUser)) {
            System.out.println("loginUser" + "为空");
            throw new UsernameNotFoundException("用户名或者密码错误！");
        } else if ("1".equals(loginUser.getStatus())) {
            throw new UserCountLockException("该用户账号被封禁，具体请联系管理员");
        }
        // TODO 查询对应的权限信息
        List<String> permissions = getUserAuthorityInfo(loginUser.getId());

        // 把数据封装为 UserDetails 对象
        MyUserDetails loginUserDetails = new MyUserDetails(loginUser, permissions);

        return loginUserDetails;
    }

    public List<String> getUserAuthorityInfo(Integer userId) {
        List<String> permissions = new ArrayList<>();
        // 根据用户id获取所有的角色信息
        LambdaQueryWrapper<Role> roleLQW = new LambdaQueryWrapper<>();
        roleLQW.inSql(Role::getId, "SELECT role_id\n" +
                "\tFROM user_role\n" +
                "\tWHERE user_id = " + userId);
        List<Role> roles = roleMapper.selectList(roleLQW);

        // 将角色信息拼接为ROLE_admin,ROLE_common的格式
        if (!roles.isEmpty()) {
            List<String> roleCodeList = roles.stream()
                    .map(role -> "ROLE_" + role.getCode())
                    .collect(Collectors.toList());
            System.out.println("roleCodeList: " + roleCodeList);
            permissions.addAll(roleCodeList);
        }

        // 遍历所有的角色，获取所有菜单权限，而且不重复
        Set<String> menuCodeSet = new HashSet<>();
        for (Role role : roles) {
            LambdaQueryWrapper<Menu> menuLQW = new LambdaQueryWrapper<>();
            menuLQW.inSql(Menu::getId, "SELECT  menu_id\n" +
                    "\tFROM role_menu\n" +
                    "\tWHERE role_id  = " + role.getId());
            List<Menu> menus = menuMapper.selectList(menuLQW);
            for (Menu menu : menus) {
                String perms = menu.getPerms();
                if (StringUtil.isNotEmpty(perms)) {
                    menuCodeSet.add(perms);
                }
            }
        }

        if (!menuCodeSet.isEmpty()) {
            permissions.addAll(menuCodeSet);
        }

        System.out.println("permissions: " + permissions);
        return permissions;
    }
}
