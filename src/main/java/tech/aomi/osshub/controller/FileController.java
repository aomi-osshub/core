package tech.aomi.osshub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aomi.common.web.controller.Result;
import tech.aomi.osshub.api.VirtualFileService;
import tech.aomi.osshub.entity.VirtualFile;
import tech.aomi.osshub.form.DirectoryCreateForm;

import javax.validation.Valid;

/**
 * @author Sean createAt 2021/10/26
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController extends AbstractController {

    @Autowired
    private VirtualFileService virtualFileService;

    /**
     * 目录创建
     */
    @PostMapping("/directories")
    public Result createDirection(@RequestBody @Valid DirectoryCreateForm form) {
        LOGGER.debug("文件夹创建: {}", form);
        VirtualFile file = virtualFileService.createDirectory(client(), form.getUserId(), form.getParent(), form.getName());
        return success(file);
    }

}
