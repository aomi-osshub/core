package tech.aomi.filexplorer.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.aomi.filexplorer.entity.Assets;

/**
 * @author Sean Create At 2020/4/25
 */
public interface AssetsServices {

    Assets query(String id);

    Page<Assets> query(String platform, String userId, Pageable pageable);

    Assets save(Assets assets);

    void del(String id);

}
