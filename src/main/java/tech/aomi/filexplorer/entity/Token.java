package tech.aomi.filexplorer.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 访问token
 *
 * @author Sean createAt 2021/10/26
 */
@Getter
@Setter
@Document
public class Token implements java.io.Serializable {
    private static final long serialVersionUID = -7494627469310583109L;

    private String id;

    /**
     * 对应的客户端ID
     */
    private String clientId;

    /**
     * 到期时间
     */
    private Date expirationAt;

    /**
     * 申请token 时用户的请求ID
     */
    private String requestId;

    /**
     * 创建时间
     */
    @Indexed(background = true, expireAfter = "105m")
    private Date createAt;


    @DBRef
    private Client client;

}
