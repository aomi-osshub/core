package tech.aomi.filexplorer.entity;

import lombok.Getter;
import lombok.Setter;
import tech.aomi.common.entity.LabelEntity;

import java.util.Date;
import java.util.Map;

/**
 * 文件信息
 *
 * @author Sean createAt 2021/10/21
 */
@Getter
@Setter
public class VirtualFile implements java.io.Serializable, LabelEntity {

    private static final long serialVersionUID = 1969492335899698927L;

    private String id;

    /**
     * 文件类型
     */
    private Type type;

    /**
     * 文件所在目录
     */
    private String directory;

    /**
     * 文件名
     */
    private String name;

    /**
     * 完整文件名
     */
    private String fullName;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件读取权限
     * 文件所有者（Owner）、用户组（Group）、其它用户（Other Users）
     */
    private String mode;

    /**
     * 文件所属客户端
     */
    private String clientId;

    /**
     * 文件所属组
     */
    private String groupId;

    /**
     * 文件所属客户端所属的用户ID
     */
    private String userId;

    /**
     * 存储类型
     */
    private StorageType storageType;

    /**
     * 存储源
     */
    private String storageSource;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 文件标签
     */
    private Map<String, Object> labels;

    public enum Type {
        /**
         * 目录
         */
        DIRECTORY,
        /**
         * 文件
         */
        FILE
    }

    public VirtualFile() {
        this.directory = "/";
        this.size = 0L;
    }


}
