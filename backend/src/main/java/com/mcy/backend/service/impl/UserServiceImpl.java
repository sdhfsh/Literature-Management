package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.common.security.MyUserDetails;
import com.mcy.backend.entity.*;
import com.mcy.backend.mapper.*;
import com.mcy.backend.service.MenuService;
import com.mcy.backend.service.UserService;
import com.mcy.backend.utils.JwtUtils;
import com.mcy.backend.utils.RedisCache;
import com.mcy.backend.utils.StringUtil;
import com.mcy.backend.vo.RegisterUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 30679
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-03-17 15:12:52
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Result login(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (Objects.isNull(authenticate)) {
//            return Result.error("用户名或密码错误");
            throw new RuntimeException("登录失败");
        }

        MyUserDetails myUserDetails = (MyUserDetails) authenticate.getPrincipal();
        User loginUser = myUserDetails.getUser();
        String jwtToken = JwtUtils.genJwtToken(loginUser.getUsername());

        // 将用户信息存储在redis中
        redisCache.setCacheObject("login:" + loginUser.getUsername(), myUserDetails);

        // 根据用户id获取所有的角色信息
        LambdaQueryWrapper<Role> roleLQW = new LambdaQueryWrapper<>();
        roleLQW.inSql(Role::getId, "SELECT role_id\n" +
                "\tFROM user_role\n" +
                "\tWHERE user_id = " + loginUser.getId());
        List<Role> roles = roleMapper.selectList(roleLQW);

        Set<Menu> menuSet = new HashSet<>();
        for (Role role : roles) {
            LambdaQueryWrapper<Menu> menuLQW = new LambdaQueryWrapper<>();
            menuLQW.inSql(Menu::getId, "SELECT  menu_id\n" +
                    "\tFROM role_menu\n" +
                    "\tWHERE role_id  = " + role.getId());
            List<Menu> menus = menuService.list(menuLQW);
            for (Menu menu : menus) {
                menuSet.add(menu);
            }
        }

        loginUser.setRoles(roles.stream().map(Role::getName).collect(Collectors.joining(",")));


        List<Menu> menuList = new ArrayList<>(menuSet);

        // 排序
        menuList.sort(Comparator.comparing(Menu::getOrderNum));

        // 转为菜单树
        List<Menu> endMenuList = menuService.buildTreeMenu(menuList);

        return Result.ok("登录成功").put("authorization", jwtToken).put("currentUser", loginUser).put("menuList", endMenuList);
    }

    @Override
    public Result register(RegisterUserVO registerUserVO) {
        String username = registerUserVO.getUsername();
        String password = registerUserVO.getPassword();
        String email = registerUserVO.getEmail();
        String phone = registerUserVO.getPhone();

        // 1. 用户名校验（非空，长度 3-20，允许字母、数字、下划线）
        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            return Result.error(400, "用户名长度必须在 3 到 20 个字符之间");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return Result.error(400, "用户名只能包含字母、数字和下划线");
        }

        // 2. 密码校验（非空，长度 6-20）
        if (password == null || password.trim().isEmpty()) {
            return Result.error(400, "密码不能为空");
        }
        if (password.length() < 6 || password.length() > 20) {
            return Result.error(400, "密码长度必须在 6 到 20 个字符之间");
        }

        // 3. 邮箱校验（非空，格式正确）
        if (email == null || email.trim().isEmpty()) {
            return Result.error(400, "邮箱不能为空");
        }
//        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
//            return Result.error(400, "邮箱格式不正确");
//        }

        if (phone == null || phone.trim().isEmpty()) {
            return Result.error(400, "手机号不能为空");
        }
        // 4. 手机号校验（符合中国手机号格式）
//        if (!phone.matches("^1[3-9]\\d{9}$")) {
//            return Result.error(400, "手机号格式不正确");
//        }

        // 5、检查用户名是否已存在
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (!Objects.isNull(user)) {
            System.out.println("User" + "不为空");
            return Result.error(400, "用户名已存在");
        }

        // 6、将用户信息存入数据库
        password = bCryptPasswordEncoder.encode(password);   // 对密码进行加密
        User registerUser = new User();
        registerUser.setUsername(username);
        registerUser.setPassword(password);
        registerUser.setAvatar(null);
        registerUser.setPhone(phone);
        registerUser.setEmail(email);
        int insert = userMapper.insert(registerUser);
        if (insert > 0) {
            // 新注册用户为普通角色
            userRoleMapper.insert(new UserRole(null, registerUser.getId(), 2));
            return Result.ok("注册成功");
        }

        return Result.error(400, "注册失败");
    }

    @Override
    public Result logout() {
        // 从 SecurityContextHolder 中获取用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        // 删除redis中的值
        boolean result = redisCache.deleteObject("login:" + username);
        return Result.ok("退出登录~~");
    }
}




