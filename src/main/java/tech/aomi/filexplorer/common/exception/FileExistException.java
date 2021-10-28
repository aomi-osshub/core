package tech.aomi.filexplorer.common.exception;

import tech.aomi.common.exception.ResourceExistException;

import java.io.Serializable;

/**
 * 文件已经存在
 *
 * @author Sean createAt 2021/10/26
 */
public class FileExistException extends ResourceExistException {
    private static final long serialVersionUID = -8835511218503136043L;

    public FileExistException() {
        super();
    }

    public FileExistException(String message) {
        super(message);
    }

    public FileExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.FILE_EXIST;
    }
}
