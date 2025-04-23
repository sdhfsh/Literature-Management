package com.mcy.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcy.backend.entity.PageBean;
import com.mcy.backend.entity.Result;
import com.mcy.backend.entity.User;
import com.mcy.backend.service.UserService;
import com.mcy.backend.utils.DateUtil;
import com.mcy.backend.vo.LoginUserVO;
import com.mcy.backend.vo.RegisterUserVO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${avatarImagesFilePath}")
    private String avatarImagesFilePath;

    @PostMapping("/login")
    public Result login(@RequestBody LoginUserVO loginUserVO) {
        System.out.println(loginUserVO);

        String username = loginUserVO.getUsername();
        String password = loginUserVO.getPassword();
        return userService.login(username, password);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterUserVO registerUserVO) {
        System.out.println("用户注册信息：" + registerUserVO);
        return userService.register(registerUserVO);
    }

    @RequestMapping("/logout")
    public Result logout() {
        return userService.logout();
    }

    /**
     * 添加或者修改用户
     * @param user
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:user:edit')" + "||" + "hasAuthority('system:user:add')")
    public Result save(@RequestBody User user) {
        System.out.println();
        if (user.getId() == null || user.getId() == -1) {

        } else {
            user.setUpdateTime(new Date());
            userService.updateById(user);
        }
        return Result.ok("保存成功");
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    @PostMapping("/updateUserPwd")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result updateUserPwd(@RequestBody User user) {
        System.out.println(user);
        User currentUser = userService.getById(user.getId());

        if (bCryptPasswordEncoder.matches(user.getOldPassword(), currentUser.getPassword())) {
            currentUser.setPassword(bCryptPasswordEncoder.encode(user.getNewPassword()));
            currentUser.setUpdateTime(new Date());
            userService.updateById(currentUser);
            return Result.ok();
        } else {
            return Result.error("旧密码输入错误！");
        }
    }

    /**
     * 上传用户头像图片
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadImage")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Map<String,Object> uploadImage(MultipartFile file)throws Exception{
        Map<String,Object> resultMap=new HashMap<>();
        if(!file.isEmpty()){
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String suffixName=originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(),new File(avatarImagesFilePath+newFileName));
            resultMap.put("code",0);
            resultMap.put("msg","上传成功");
            Map<String,Object> dataMap=new HashMap<>();
            dataMap.put("title",newFileName);
            dataMap.put("src","image/userAvatar/"+newFileName);
            resultMap.put("data",dataMap);
        }
        return resultMap;
    }

    /**
     * 修改用户头像
     * @param user
     * @return
     */
    @RequestMapping("/updateAvatar")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result updateAvatar(@RequestBody User user){
        User currentUser = userService.getById(user.getId());
        currentUser.setUpdateTime(new Date());
        currentUser.setAvatar(user.getAvatar());
        userService.updateById(currentUser);
        return Result.ok("更换成功");
    }

    /**
     * 根据条件，分页查询用户信息
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result list(@RequestBody PageBean pageBean) {
        Page<User> pageResult = userService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()));
        List<User> userList = pageResult.getRecords();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userList", userList);
        resultMap.put("total", pageResult.getTotal());
        return Result.ok(resultMap);
    }
}
