package tech.aomi.osshub.common.exception;

/**
 * @author Sean createAt 2021/10/22
 */
public enum ErrorCode {
    // 3400
    CLIENT_EXIST("3401", "客户端已经存在"),
    CLIENT_NON_EXIST("3402", "客户端不存在"),
    // 3449

    // 3500 开始
    DIR_CREATE("3500", "目录创建失败"),
    DIR_EXIST("3501", "目录已经存在"),
    DIR_NON_EXIST("3502", "目录不存在"),
    // 3549 结束

    // 3550 开始
    FILE_CREATE("3550", "文件创建时间"),
    FILE_EXIST("3551", "文件已经存在"),
    FILE_NON_EXIST("3552", "文件不存在")
    // 3649 结束
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "[" + this.code + "]" + this.message;
    }


}
