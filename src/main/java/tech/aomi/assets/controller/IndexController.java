package tech.aomi.assets.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tech.aomi.assets.api.AssetsServices;
import tech.aomi.assets.common.exception.FileNonExistException;
import tech.aomi.assets.entity.Assets;
import tech.aomi.common.exception.ErrorCode;
import tech.aomi.common.web.controller.AbstractController;
import tech.aomi.common.web.controller.Result;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Sean Create At 2020/4/25
 */
@Slf4j
@Controller
public class IndexController extends AbstractController {

    @Autowired
    private AssetsServices assetsServices;

    @Autowired
    private MultipartProperties properties;

//    @GetMapping("/")
//    public Result showAll(String platform, String userId, Pageable pageable) {
//        return success(assetsServices.query(platform, userId, pageable));
//    }

    @GetMapping("/{id}")
    public String showOne(@PathVariable String id) throws UnsupportedEncodingException {
        LOGGER.debug("获取文件: {}", id);
        File dir = new File(properties.getLocation() + File.separator + id);
        if (!dir.exists()) {
            LOGGER.error("文件不存在: {}", id);
            throw new FileNonExistException(id);
        }
        File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            return "forward:" + id + File.separator + URLEncoder.encode(files[0].getName(), "UTF-8");
        }
        throw new FileNonExistException(id);
    }

    @PostMapping("/")
    public Result upload(@RequestParam MultipartFile file,
                         String platform,
                         String userId
    ) {
        Assets assets = new Assets();
        assets.setName(file.getOriginalFilename());
        assets.setSize(file.getSize());
        assets.setPlatform(platform);
        assets.setUserId(userId);
        assets = assetsServices.save(assets);

        try {
            String filepath = properties.getLocation() + File.separator + assets.getId();
            File dir = new File(filepath);
            boolean ok = dir.mkdirs();
            if (!ok) {
                assetsServices.del(assets.getId());
                return Result.create(ErrorCode.EXCEPTION.getCode(), "创建目录失败");
            }
            String fullPath = filepath + File.separator + assets.getName();
            LOGGER.debug("文件路径: {}", fullPath);
            file.transferTo(new File(fullPath));
            Result result = success();
            result.put("id", assets.getId());
            result.put("filepath", assets.getId() + "/" + assets.getName());
            result.put("name", assets.getName());
            result.put("size", assets.getSize());
            return result;
        } catch (Exception e) {
            LOGGER.error("保存文件失败: {}", e.getMessage(), e);
            assetsServices.del(assets.getId());
            return Result.create(ErrorCode.EXCEPTION.getCode(), "保存文件失败");
        }
    }


}
