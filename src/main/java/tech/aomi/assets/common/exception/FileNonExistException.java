package tech.aomi.assets.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import tech.aomi.common.exception.ResourceNonExistException;

/**
 * @author Sean Create At 2020/4/25
 */
public class FileNonExistException extends RuntimeException {
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
}
