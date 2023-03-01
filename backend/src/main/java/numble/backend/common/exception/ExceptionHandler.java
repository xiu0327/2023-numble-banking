package numble.backend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorFormat> businessException(BusinessException e){
        return new ResponseEntity<>(ErrorFormat.create(e.getBasicExceptionType()), e.getBasicExceptionType().getHttpStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorFormat> authenticationException(AuthenticationException e){
        return new ResponseEntity<>(new ErrorFormat("AUTH_ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorFormat> accessDeniedException(AccessDeniedException e){
        return new ResponseEntity<>(new ErrorFormat("ACCESS_ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorFormat> notResolveException(Exception e){
        return new ResponseEntity<>(ErrorFormat.create(InternalServerExceptionType.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorFormat> dtoValidationException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors())
        {
            stringBuilder.append("[")
                    .append(fieldError.getField())
                    .append("](은)는 ")
                    .append(fieldError.getDefaultMessage())
                    .append(" 입력된 값: [")
                    .append(fieldError.getRejectedValue())
                    .append("]");
        }

        return new ResponseEntity<>(new ErrorFormat("DTO_VALIDATION_ERROR", stringBuilder.toString()), HttpStatus.BAD_REQUEST);
    }
}
