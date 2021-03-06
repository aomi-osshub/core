package tech.aomi.osshub.entity;

import lombok.Getter;
import lombok.Setter;
import tech.aomi.common.entity.LabelEntity;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 客户端信息
 *
 * @author Sean createAt 2021/10/22
 */
@Getter
@Setter
public class Client implements java.io.Serializable, LabelEntity {

    private static final long serialVersionUID = -1170529289239704415L;

    /**
     * 客户端唯一id同时作为accessKey
     */
    private String id;

    private String name;

    private String secretKey;

    private String describe;

    /**
     * 存储方式
     */
    private StorageType storageType;

    /**
     * 客户端对应的标签信息
     * 用于存储其他参数
     */
    private Map<String, Object> labels;

    /**
     * 权限信息
     */
    private Set<String> authorities;

    /**
     * 允许上传的文件扩展名
     */
    private Set<String> filenameExtensions;

    private Date createAt;
}
