package com.mcy.backend.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcy.backend.entity.*;
import com.mcy.backend.service.*;
import com.mcy.backend.utils.CitationUtil;
import com.mcy.backend.utils.StringUtil;
import com.mcy.backend.vo.DocumentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${documentFilePath}")
    private String documentFilePath;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DocCategoryService docCategoryService;

    @Autowired
    private DocumentStatusService documentStatusService;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result upload(@RequestParam("file") MultipartFile file) {
        synchronized (FileController.class) {
            String flag = System.currentTimeMillis() + "";
            // 获取原始文件名
            String filename = file.getOriginalFilename();

            // 提取文件后缀名
            String suffixName = filename.substring(filename.lastIndexOf(".") + 1);
            String baseName = filename.substring(0, filename.lastIndexOf("."));
            String newFileName = flag + "-" + baseName + "." + suffixName;

            // 文件存储形式：时间戳 + 文件名
            try {

                FileUtil.writeBytes(file.getBytes(), documentFilePath + newFileName);
                System.out.println(filename + "上传成功");
                Thread.sleep(1);
            } catch (Exception e) {
                System.out.println(filename + "上传失败");
            }
            Map<String, Object> resultMap = new HashMap<>();
//            resultMap.put("flag", flag);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("file_type", suffixName);
            dataMap.put("file_path", "file/download/" + newFileName);
            resultMap.put("data", dataMap);
            return Result.ok(resultMap);
        }
    }

    @GetMapping("/download")
    @PreAuthorize("hasAuthority('system:user:add')")
    public void getDoc(@RequestParam String filePath, HttpServletResponse response) {
        System.out.println("filePath: " + filePath);
        try {
            String fileName = filePath.replace("file/download/", "");
            String fullPath = documentFilePath + fileName;

            // 解析原始文件名（去掉时间戳前缀）
            String originalFileName = fileName.substring(fileName.indexOf("-") + 1);

            if (!FileUtil.exist(fullPath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            byte[] fileBytes = FileUtil.readBytes(fullPath);

            // 根据文件类型设置响应内容类型
            if (fileName.endsWith(".pdf")) {
                response.setContentType("application/pdf");
            } else if (fileName.endsWith(".docx")) {
                response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            } else {
                response.setContentType("application/octet-stream");
            }

            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(originalFileName, "UTF-8"));
            response.setContentLength(fileBytes.length);

            OutputStream os = response.getOutputStream();
            os.write(fileBytes);
            os.flush();
            os.close();

            System.out.println("文件下载成功：" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 保存文献信息
     *
     * @param documentVO
     * @return
     */
    @Transactional
    @PostMapping("/sava")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result savaDOC(@RequestBody DocumentVO documentVO) {
        System.out.println(documentVO);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        Document document = new Document();

        if (documentVO.getId() == null || documentVO.getId() == -1) {
            // 新增
            document.setId(null);
            document.setTitle(documentVO.getTitle());
            document.setAuthor(documentVO.getAuthor());
            document.setExcerpt(documentVO.getExcerpt());
            document.setJournal(documentVO.getJournal());
            document.setPublicationDate(documentVO.getPublicationDate());
            document.setPublisher(documentVO.getPublisher());
            document.setVolume(documentVO.getVolume());
            document.setIssue(documentVO.getIssue());
            document.setPages(documentVO.getPages());
            document.setDoi(documentVO.getDoi());
            document.setUrl(documentVO.getUrl());
            document.setType(documentVO.getType());
            document.setFilePath(documentVO.getFilePath());
            document.setFileType(documentVO.getFileType());
            document.setCreatedAt(new Date());
            document.setUpdatedAt(new Date());
            document.setOwnerId(loginUser.getId());
            documentService.save(document);

            DocumentStatus documentStatus = new DocumentStatus();
            documentStatus.setId(null);
            documentStatus.setDocumentId(document.getId());
            documentStatus.setStatus(documentVO.getStatus());
            documentStatusService.save(documentStatus);

            // 保存文献标签
            documentVO.getCategory().forEach(c -> {
                DocCategory docCategory = new DocCategory();
                docCategory.setId(null);
                docCategory.setDocumentId(document.getId());
                docCategory.setCategoryName(c);
                docCategoryService.save(docCategory);
            });

        } else {
            // 修改
            Document document1 = documentService.getById(documentVO.getId());
            document.setId(documentVO.getId());
            document.setTitle(documentVO.getTitle());
            document.setAuthor(documentVO.getAuthor());
            document.setExcerpt(documentVO.getExcerpt());
            document.setJournal(documentVO.getJournal());
            document.setPublicationDate(documentVO.getPublicationDate());
            document.setPublisher(documentVO.getPublisher());
            document.setVolume(documentVO.getVolume());
            document.setIssue(documentVO.getIssue());
            document.setPages(documentVO.getPages());
            document.setDoi(documentVO.getDoi());
            document.setUrl(documentVO.getUrl());
            document.setType(documentVO.getType());
            document.setFilePath(documentVO.getFilePath());
            document.setFileType(documentVO.getFileType());
            document.setCreatedAt(document1.getCreatedAt());
            document.setUpdatedAt(new Date());
            document.setOwnerId(document1.getOwnerId());
            documentService.updateById(document);

            // 更新文献状态
            DocumentStatus status = documentStatusService.getOne(new QueryWrapper<DocumentStatus>().eq("document_id", documentVO.getId()));
            status.setStatus(documentVO.getStatus());
            documentStatusService.updateById(status);

            // 保存文献标签
            docCategoryService.remove(new QueryWrapper<DocCategory>().in("document_id", documentVO.getId()));
            documentVO.getCategory().forEach(c -> {
                DocCategory docCategory = new DocCategory();
                docCategory.setId(null);
                docCategory.setDocumentId(document.getId());
                docCategory.setCategoryName(c);
                System.out.println(docCategory);
                docCategoryService.save(docCategory);
            });
        }

        return Result.ok("保存成功");
    }

    @GetMapping("/tags")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result getTags() {
        List<Category> list = categoryService.list();
        List<String> tagNameList = list.stream().map(category -> category.getName()).collect(Collectors.toList());
        return Result.ok().put("tags", tagNameList);
    }

    /**
     * 根据条件，分页查询用户信息
     *
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result list(@RequestBody PageBean pageBean) {
        // 获取当前登录用户
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loginUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        String query = pageBean.getQuery().trim();
        Page<Document> pageResult = documentService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),
                new QueryWrapper<Document>()
                        .eq("owner_id", loginUser.getId())
                        .like(StringUtil.isNotEmpty(query), "title", query));
        List<Document> docList = pageResult.getRecords();
        System.out.println("docList: " + docList);
        List<DocumentVO> resDocList = new ArrayList<>();
        for (Document document : docList) {
            // 获取文献标签
            List<DocCategory> docCategoryList = docCategoryService.list(new LambdaQueryWrapper<DocCategory>().eq(DocCategory::getDocumentId, document.getId()));
            List<String> categoryList = docCategoryList.stream().map(docCategory -> docCategory.getCategoryName()).collect(Collectors.toList());

            // 获取文献的上传者
            User ownUser = userService.getById(document.getOwnerId());

            DocumentStatus docStatus = documentStatusService.getOne(new LambdaQueryWrapper<DocumentStatus>().eq(DocumentStatus::getDocumentId, document.getId()));
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
            documentVO.setStatus(docStatus.getStatus());
            documentVO.setFilePath(document.getFilePath());
            documentVO.setFileType(document.getFileType());
            documentVO.setCreatedAt(document.getCreatedAt());
            documentVO.setUpdatedAt(document.getUpdatedAt());
            documentVO.setUser(ownUser);

            resDocList.add(documentVO);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("docList", resDocList);
        resultMap.put("total", pageResult.getTotal());
        System.out.println(resultMap);
        return Result.ok(resultMap);
    }

    /**
     * 删除 / 批量删除
     *
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result delete(@RequestBody Integer[] ids) {
        for (Integer id : ids) {
            Document document = documentService.getById(id);
            String filePath = document.getFilePath();
            if (filePath.isEmpty()) {
                continue;
            }

            String fileName = filePath.replace("file/download/", "");
            FileUtil.del(documentFilePath + fileName);
        }

        documentService.removeByIds(Arrays.asList(ids));
        docCategoryService.remove(new QueryWrapper<DocCategory>().in("document_id", ids));
        documentStatusService.remove(new LambdaQueryWrapper<DocumentStatus>().in(DocumentStatus::getDocumentId, ids));

        return Result.ok();
    }

    /**
     * 修改功能，需要先根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result findById(@PathVariable(value = "id") Integer id) {
        Document document = documentService.getById(id);
        List<DocCategory> docCategoryList = docCategoryService.list(new QueryWrapper<DocCategory>().eq("document_id", id));
        List<String> tags = docCategoryList.stream().map(docCategory -> docCategory.getCategoryName()).collect(Collectors.toList());
        DocumentStatus status = documentStatusService.getOne(new QueryWrapper<DocumentStatus>().eq("document_id", id));

        DocumentVO documentVO = new DocumentVO();

        documentVO.setId(document.getId());
        documentVO.setTitle(document.getTitle());
        documentVO.setAuthor(document.getAuthor());
        documentVO.setExcerpt(document.getExcerpt());
        documentVO.setJournal(document.getJournal());
        documentVO.setCategory(tags);
        documentVO.setPublicationDate(document.getPublicationDate());
        documentVO.setPublisher(document.getPublisher());
        documentVO.setVolume(document.getVolume());
        documentVO.setIssue(document.getIssue());
        documentVO.setPages(document.getPages());
        documentVO.setDoi(document.getDoi());
        documentVO.setUrl(document.getUrl());
        documentVO.setType(document.getType());
        documentVO.setStatus(status.getStatus());
        documentVO.setFilePath(document.getFilePath());
        documentVO.setFileType(document.getFileType());

        Map<String, Object> map = new HashMap<>();
        map.put("document", documentVO);
        return Result.ok(map);
    }

    @GetMapping("/reference")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result generateCitation(@RequestParam Integer docId, @RequestParam String style) {
        System.out.println("docId: " + docId + ", style: " + style);
        String quote = CitationUtil.generate(documentService.getById(docId), style);
        return Result.ok().put("quote", quote);
    }

    /**
     * 更改文献状态
     *
     * @param documentVO
     * @return
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result updateStatus(@RequestBody DocumentVO documentVO) {
        System.out.println(documentVO);
        DocumentStatus documentStatus = documentStatusService.getOne(new LambdaQueryWrapper<DocumentStatus>().eq(DocumentStatus::getDocumentId, documentVO.getId()));
        documentStatus.setStatus(documentVO.getStatus());
        documentStatusService.updateById(documentStatus);
        return Result.ok();
    }

    /**
     * 在线预览功能
     *
     * @param filePath
     * @param response
     */
    @GetMapping("/preview")
    @PreAuthorize("hasAuthority('system:user:add')")
    public void previewDoc(@RequestParam String filePath, HttpServletResponse response) {
        System.out.println("filePath: " + filePath);
        try {
            String fileName = filePath.replace("file/download/", "");
            String fullPath = documentFilePath + fileName;

            File file = new File(fullPath);
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!fileName.toLowerCase().endsWith(".pdf")) {
                response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"msg\": \"仅支持PDF文件预览\"}");
                return;
            }

            response.setContentType("application/pdf");
            String originalFileName = fileName.substring(fileName.indexOf("-") + 1);
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(originalFileName, "UTF-8"));
            response.setContentLengthLong(file.length());

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }

            System.out.println("PDF 预览成功：" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
