package tech.aomi.assets.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Sean Create At 2020/4/25
 */
@Getter
@Setter
public class Assets implements java.io.Serializable {

    private static final long serialVersionUID = -8436389282609026448L;

    private String id;

    private String name;

    private String type;

    /**
     * 单位: B
     */
    private Long size = 0L;

    private String platform;

    private String userId;

    private Date createAt;

}
