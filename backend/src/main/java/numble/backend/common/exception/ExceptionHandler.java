package numble.backend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorFormat> businessException(BusinessException e){
        return new ResponseEntity<>(ErrorFormat.create(e.getBasicExceptionType()), e.getBasicExceptionType().getHttpStatus());
    }
}
