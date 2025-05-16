package com.mcy.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcy.backend.entity.*;
import com.mcy.backend.service.*;
import com.mcy.backend.utils.StringUtil;
import com.mcy.backend.vo.DocumentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private CrowdService crowdService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCrowdService userCrowdService;

    @Autowired
    private CrowdApplicationService crowdApplicationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocCategoryService docCategoryService;

    @Autowired
    private DocumentStatusService documentStatusService;


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result save(@RequestBody Crowd crowd) {
        System.out.println("crowd: " + crowd);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if (crowd.getId() == -1) {
            System.out.println("小组新增功能");
            crowd.setId(null);
            crowd.setCreatedBy(loginUser.getId());
            crowd.setCreatedAt(new Date());

            crowdService.save(crowd);
        } else {
            System.out.println("小组修改功能");
            Crowd crowd1 = crowdService.getById(crowd.getId());
            crowd1.setGroupName(crowd.getGroupName());
            crowd1.setDescription(crowd.getDescription());
            crowdService.updateById(crowd1);
        }

        return Result.ok();
    }

    /**
     * 删除 / 批量删除小组
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result delete(@RequestBody Integer[] ids) {
        crowdService.removeByIds(Arrays.asList(ids));
        userCrowdService.remove(new LambdaQueryWrapper<UserCrowd>().in(UserCrowd::getCrowdId, Arrays.asList(ids)));
        return Result.ok();
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result list(@RequestBody PageBean pageBean) {
        String query = pageBean.getQuery().trim();
        Page<Crowd> pageResult = crowdService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()), new QueryWrapper<Crowd>().like(StringUtil.isNotEmpty(query), "group_name", query));
        List<Crowd> crowdList = pageResult.getRecords();
        System.out.println("crowdList: " + crowdList);

        for (Crowd crowd : crowdList) {
            int count = userCrowdService.count(new QueryWrapper<UserCrowd>().eq("crowd_id", crowd.getId()));
            crowd.setMemberNum(count);

            // 封装每个小组申请未通过的全部列表
            List<CrowdApplication> crowdApplications = crowdApplicationService
                    .list(new QueryWrapper<CrowdApplication>().eq("status", "0").eq("crowd_id", crowd.getId()));
            crowd.setCrowdApplications(crowdApplications);
        }

        return Result.ok().put("crowdList", crowdList).put("total", pageResult.getTotal());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result getCrowd(@PathVariable(value = "id") Integer id) {
        System.out.println("crowd_id: " + id);
        Crowd crowd = crowdService.getById(id);
        if (Objects.isNull(crowd)) {
            return Result.error("该小组不存在");
        }
        return Result.ok().put("crowd", crowd);
    }

    @PostMapping("/join")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result join(@RequestBody Integer groupId) {
        System.out.println("groupId: " + groupId);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        CrowdApplication crowdApplication = new CrowdApplication();
        crowdApplication.setId(null);
        crowdApplication.setUserId(loginUser.getId());
        crowdApplication.setCrowdId(groupId);
        crowdApplication.setStatus("0");
        crowdApplication.setApplyTime(new Date());
        crowdApplicationService.save(crowdApplication);
        return Result.ok("申请成功");
    }

    @GetMapping("/application")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result application() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        UserCrowd userCrowd = userCrowdService.getOne(
                new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getUserId, loginUser.getId())
        );

        // 用户未加入小组
        if (userCrowd == null) {
            return Result.error(407, "您尚未加入小组");
        }

        CrowdApplication application = crowdApplicationService.getOne(
                new LambdaQueryWrapper<CrowdApplication>().eq(CrowdApplication::getUserId, loginUser.getId())
        );

        // 没有申请记录，说明是直接加入的
        if (application == null) {
            // 根据当前用户所在小组，查询小组信息
            Crowd joinCrowd = crowdService.getById(userCrowd.getCrowdId());
            int members = userCrowdService.count(
                    new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getCrowdId, joinCrowd.getId())
            );
            joinCrowd.setMemberNum(members);
            return Result.ok().put("joinCrowd", joinCrowd);
        }

        // 申请审核中
        if ("0".equals(application.getStatus())) {
            return Result.error(408, "审核中").put("application", application);
        }

        // 审核通过的情况
        Crowd joinCrowd = crowdService.getById(application.getCrowdId());
        int members = userCrowdService.count(
                new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getCrowdId, joinCrowd.getId())
        );
        joinCrowd.setMemberNum(members);
        return Result.ok().put("joinCrowd", joinCrowd);
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result apply(@RequestBody CrowdApplication crowdApplication) {
        System.out.println(crowdApplication);
        crowdApplication.setStatus("1");
        crowdApplication.setDecisionTime(new Date());
        crowdApplicationService.updateById(crowdApplication);
        crowdApplicationService.removeById(crowdApplication.getId());
        userCrowdService.save(new UserCrowd(null, crowdApplication.getUserId(), crowdApplication.getCrowdId(), new Date()));
        return Result.ok();
    }

    @PostMapping("/doc/list")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result docList(@RequestBody PageBean pageBean) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        // 获取当前登录用户的小组
        UserCrowd userCrowd = userCrowdService.getOne(new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getUserId, loginUser.getId()));
        // 获取当前登录用户所属小组的全部成员id
        List<UserCrowd> list = userCrowdService.list(new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getCrowdId, userCrowd.getCrowdId()));
        List<Integer> crowdMemberIds = list.stream().map(c -> c.getUserId()).collect(Collectors.toList());

        // 分页获取当前小组内成员的全部文献
        String query = pageBean.getQuery().trim();
        Page<Document> pageResult = documentService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),
                new QueryWrapper<Document>()
                        .in("owner_id", crowdMemberIds)
                        .like(StringUtil.isNotEmpty(query), "title", query));
        List<Document> docList = pageResult.getRecords();
        System.out.println("docList: " + docList);

        List<DocumentVO> resDocList = new ArrayList<>();
        long total = pageResult.getTotal();
        for (Document document : docList) {
            // 获取文献的状态（仅返回支持小组共享的文献）
            DocumentStatus documentStatus = documentStatusService.getOne(new LambdaQueryWrapper<DocumentStatus>().eq(DocumentStatus::getDocumentId, document.getId()));
            if ("group".equals(documentStatus.getStatus())) {
                // 获取文献标签
                List<DocCategory> docCategoryList = docCategoryService.list(new LambdaQueryWrapper<DocCategory>().eq(DocCategory::getDocumentId, document.getId()));
                List<String> categoryList = docCategoryList.stream().map(docCategory -> docCategory.getCategoryName()).collect(Collectors.toList());

                // 获取文献的上传者
                User ownUser = userService.getById(document.getOwnerId());

                DocumentVO documentVO = new DocumentVO();
                documentVO.setId(document.getId());
                documentVO.setTitle(document.getTitle());
                documentVO.setAuthor(document.getAuthor());
                documentVO.setExcerpt(document.getExcerpt());
                documentVO.setJournal(document.getJournal());
                documentVO.setCategory(categoryList);
                documentVO.setPublicationDate(document.getPublicationDate());
                documentVO.setPublisher(document.getPublisher());
                documentVO.setVolume(document.getVolume());
                documentVO.setIssue(document.getIssue());
                documentVO.setPages(document.getPages());
                documentVO.setDoi(document.getDoi());
                documentVO.setUrl(document.getUrl());
                documentVO.setType(document.getType());
                documentVO.setStatus(null);
                documentVO.setFilePath(document.getFilePath());
                documentVO.setFileType(document.getFileType());
                documentVO.setCreatedAt(document.getCreatedAt());
                documentVO.setUpdatedAt(document.getUpdatedAt());
                documentVO.setUser(ownUser);

                resDocList.add(documentVO);
            } else {
                total--;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("docList", resDocList);
        resultMap.put("total", total);
        System.out.println(resultMap);
        return Result.ok(resultMap);
    }

    @PostMapping("/exit")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result exit() {
        // 获取当前登录用户
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        boolean removeRes = userCrowdService.remove(new LambdaQueryWrapper<UserCrowd>().eq(UserCrowd::getUserId, loginUser.getId()));
        if (removeRes) {
            return Result.ok("成功退出小组");
        } else {
            return Result.error("退出小组失败");
        }
    }
}
