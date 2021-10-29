package tech.aomi.osshub.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * 目录创建表单
 *
 * @author Sean createAt 2021/10/26
 */
@Getter
@Setter
@ToString
public class DirectoryCreateForm {

    /**
     * 上级目录
     */
    private String parent = "/";

    /**
     * 用户id
     */
    @NotEmpty
    private String userId;

    /**
     * 当前目录名称
     */
    @NotEmpty
    @Length(max = 255)
    private String name;
}
