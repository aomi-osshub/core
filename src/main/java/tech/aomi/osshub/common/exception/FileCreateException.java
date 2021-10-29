package tech.aomi.osshub.common.exception;

import tech.aomi.common.exception.ServiceException;

import java.io.Serializable;

/**
 * 文件创建失败
 *
 * @author Sean createAt 2021/10/22
 */
public class FileCreateException extends ServiceException {
    private static final long serialVersionUID = -2213975897273390725L;

    public FileCreateException() {
        super();
    }

    public FileCreateException(String message) {
        super(message);
    }

    public FileCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileCreateException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.FILE_CREATE;
    }
}
