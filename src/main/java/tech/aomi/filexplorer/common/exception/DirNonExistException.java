package tech.aomi.filexplorer.common.exception;

import tech.aomi.common.exception.ResourceNonExistException;

import java.io.Serializable;

/**
 * 目录不存在
 *
 * @author Sean createAt 2021/10/22
 */
public class DirNonExistException extends ResourceNonExistException {
    private static final long serialVersionUID = 7342753686924672125L;

    public DirNonExistException() {
        super();
    }

    public DirNonExistException(String message) {
        super(message);
    }

    public DirNonExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirNonExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return super.getErrorCode();
    }
}
