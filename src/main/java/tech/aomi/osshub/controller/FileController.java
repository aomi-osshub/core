package tech.aomi.osshub.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.aomi.common.exception.ServiceException;
import tech.aomi.common.web.controller.Result;
import tech.aomi.osshub.CoreProperties;
import tech.aomi.osshub.api.VirtualFileService;
import tech.aomi.osshub.common.exception.FileNonExistException;
import tech.aomi.osshub.entity.StorageType;
import tech.aomi.osshub.entity.VirtualFile;
import tech.aomi.osshub.form.DirectoryCreateForm;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Sean createAt 2021/10/26
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController extends AbstractController {

    @Autowired
    private VirtualFileService virtualFileService;

    @Autowired
    private CoreProperties properties;

    @Autowired
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @GetMapping
    public Result showAll(@RequestParam(defaultValue = "/") String directory) {
        List<VirtualFile> files = virtualFileService.query(client().getId(), directory);
        return success(files);
    }

    @GetMapping("/{id}/**")
    public void getOne(
            @PathVariable String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        VirtualFile virtualFile = virtualFileService.visit(id).orElseThrow(() -> new FileNonExistException("文件不存在"));

        StorageType storageType = virtualFile.getStorageType();

        Path filePath;

        switch (storageType) {
            case FILE_SYSTEM: {
                String path = properties.getRootDir() + File.separator + virtualFile.getStorageSource();
                filePath = Paths.get(path);
                if (!Files.exists(filePath)) {
                    throw new FileNonExistException("文件不存在:" + virtualFile.getName());
                }
                break;
            }
            default:
                throw new ServiceException("不支持的存储类型:" + storageType);
        }

        String mimeType = Files.probeContentType(filePath);
        if (StringUtils.isNotEmpty(mimeType)) {
            response.setContentType(mimeType);
        }

        request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, filePath);
        nonStaticResourceHttpRequestHandler.handleRequest(request, response);
    }

    /**
     * 目录创建
     */
    @PostMapping("/directories")
    public Result createDirection(@RequestBody @Valid DirectoryCreateForm form) {
        LOGGER.debug("文件夹创建: {}", form);
        VirtualFile file = virtualFileService.createDirectory(client(), form.getUserId(), form.getParent(), form.getName());
        return success(file);
    }

    /**
     * 图片上传
     */
    @PostMapping
    public Result upload(
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "/") String directory,
            String groupId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "700") String mode
    ) throws IOException {

        VirtualFile virtualFile = new VirtualFile();
        virtualFile.setType(VirtualFile.Type.FILE);
        virtualFile.setDirectory(directory);
        virtualFile.setName(file.getOriginalFilename());
        virtualFile.setSize(file.getSize());
        virtualFile.setMode(mode);

        virtualFile.setGroupId(groupId);
        virtualFile.setUserId(userId);

        virtualFile = virtualFileService.save(client(), virtualFile, file.getInputStream());

        return success(virtualFile);
    }

}
