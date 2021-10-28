package tech.aomi.filexplorer.common.exception;

import tech.aomi.common.exception.ResourceNonExistException;

import java.io.Serializable;

/**
 * @author Sean Create At 2020/4/25
 */
public class FileNonExistException extends ResourceNonExistException {
    private static final long serialVersionUID = 5139561357812030934L;

    public FileNonExistException() {
        super();
    }

    public FileNonExistException(String message) {
        super(message);
    }

    public FileNonExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNonExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.FILE_NON_EXIST;
    }
}
