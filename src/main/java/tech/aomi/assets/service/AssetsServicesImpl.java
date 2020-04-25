package tech.aomi.assets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.sitb.spring.data.mongo.repository.DocumentRepository;
import software.sitb.spring.data.mongo.repository.QueryBuilder;
import tech.aomi.assets.api.AssetsServices;
import tech.aomi.assets.entity.Assets;
import tech.aomi.assets.repositry.AssetsRepository;
import tech.aomi.common.exception.ResourceNonExistException;

import java.util.Date;

/**
 * @author Sean Create At 2020/4/25
 */
@Service
public class AssetsServicesImpl implements AssetsServices {

    @Autowired
    private AssetsRepository assetsRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Assets query(String id) {
        return assetsRepository.findById(id).orElseThrow(() -> new ResourceNonExistException("文件不存在: {}" + id));
    }

    @Override
    public Page<Assets> query(String platform, String userId, Pageable pageable) {
        QueryBuilder builder = QueryBuilder.builder();
        if (StringUtils.hasLength(platform)) {
            builder.is("platform", platform);
        }
        if (StringUtils.hasLength(userId)) {
            builder.is("userId", userId);
        }

        return documentRepository.findAll(Assets.class, builder.build(), pageable);
    }

    @Override
    public Assets save(Assets assets) {
        assets.setCreateAt(new Date());
        return assetsRepository.save(assets);
    }

    @Override
    public void del(String id) {
        assetsRepository.deleteById(id);
    }

}
