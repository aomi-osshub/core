package tech.aomi.filexplorer.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Sean createAt 2021/10/22
 */
@Getter
@Setter
@ToString
public class TokenForm {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date timestamp;

    /**
     * 请求ID，保证唯一
     * 申请过的id不能再次申请
     */
    @NotEmpty
    private String requestId;

    /**
     * 过期时间
     * 单位秒
     * 最长2个小时
     * 默认值300秒
     */
    @Min(0)
    @Max(7200)
    private Integer expireAfter = 300;

}
