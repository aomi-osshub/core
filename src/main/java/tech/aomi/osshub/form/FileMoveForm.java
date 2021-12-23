package tech.aomi.osshub.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 文件移动表单
 *
 * @author Sean createAt 2021/12/23
 */
@Getter
@Setter
@ToString
public class FileMoveForm {

    /**
     * 源文件ID
     */
    @NotEmpty
    private List<String> sourceIds;

    /**
     * 目标文件夹ID
     */
    @NotEmpty
    private String targetId;
}
