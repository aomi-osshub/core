package tech.aomi.filexplorer.constant;

/**
 * 权限定义
 *
 * @author Sean createAt 2021/10/26
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum Authority {
    FILE_ALL("文件-所有"),
    FILE_BROWSE("文件-浏览"),
    FILE_READ("文件-读取"),
    FILE_WRITE("文件-写入"),
    //
    ;

    private final String describe;


    Authority(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return this.describe;
    }

}
