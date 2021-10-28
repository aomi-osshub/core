package tech.aomi.filexplorer.controller.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tech.aomi.common.web.controller.Result;
import tech.aomi.filexplorer.api.VirtualFileService;
import tech.aomi.filexplorer.controller.AbstractController;
import tech.aomi.filexplorer.entity.VirtualFile;

import java.io.IOException;

/**
 * 图片媒体服务
 *
 * @author Sean createAt 2021/10/21
 */
@RestController
@RequestMapping("/files/images")
public class ImageController extends AbstractController {

    @Autowired
    private VirtualFileService virtualFileService;

    /**
     * 图片上传
     */
    @PostMapping
    public Result upload(
            @RequestParam MultipartFile file,
            String directory,
            String groupId,
            String userId,
            @RequestParam(defaultValue = "700") String mode,
            @RequestParam(defaultValue = "NONE") String labelType
    ) throws IOException {

        VirtualFile virtualFile = new VirtualFile();
        virtualFile.setType(VirtualFile.Type.FILE);
        virtualFile.setDirectory(directory);
        virtualFile.setName(file.getOriginalFilename());
        virtualFile.setSize(file.getSize());
        virtualFile.setMode(mode);

        virtualFile.setGroupId(groupId);
        virtualFile.setUserId(userId);

        virtualFile.addLabel("type", labelType);

        virtualFile = virtualFileService.save(client(), virtualFile, file.getInputStream());

        return success(virtualFile);
    }


}
