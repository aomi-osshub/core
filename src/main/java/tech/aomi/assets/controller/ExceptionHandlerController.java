package tech.aomi.assets.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.aomi.assets.common.exception.FileNonExistException;

/**
 * @author Sean Create At 2020/4/25
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(FileNonExistException.class)
    public ResponseEntity<String> exception(FileNonExistException ex) {
        return new ResponseEntity<>("file not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
