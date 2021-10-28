package tech.aomi.filexplorer.common.exception;

import tech.aomi.common.exception.ResourceExistException;

import java.io.Serializable;

/**
 * @author Sean createAt 2021/10/27
 */
public class DirExistException extends ResourceExistException {
    private static final long serialVersionUID = -1495377402258047305L;

    public DirExistException() {
        super();
    }

    public DirExistException(String message) {
        super(message);
    }

    public DirExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.DIR_EXIST;
    }
}
