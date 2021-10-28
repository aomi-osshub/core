package tech.aomi.filexplorer.common.exception;

import tech.aomi.common.exception.ServiceException;

import java.io.Serializable;

/**
 * 目录创建异常
 *
 * @author Sean createAt 2021/10/22
 */
public class DirCreateException extends ServiceException {
    private static final long serialVersionUID = 3796001835852805326L;

    public DirCreateException() {
        super();
    }

    public DirCreateException(String message) {
        super(message);
    }

    public DirCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirCreateException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.DIR_CREATE;
    }
}
