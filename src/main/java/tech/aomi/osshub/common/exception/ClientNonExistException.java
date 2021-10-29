package tech.aomi.osshub.common.exception;

import tech.aomi.common.exception.ResourceNonExistException;

import java.io.Serializable;

/**
 * @author Sean createAt 2021/10/22
 */
public class ClientNonExistException extends ResourceNonExistException {
    private static final long serialVersionUID = -4320172791060961312L;

    public ClientNonExistException() {
        super();
    }

    public ClientNonExistException(String message) {
        super(message);
    }

    public ClientNonExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientNonExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Serializable getErrorCode() {
        return ErrorCode.CLIENT_NON_EXIST;
    }
}
