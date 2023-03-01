package numble.backend.authority.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numble.backend.common.exception.BasicExceptionType;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthorityExceptionType implements BasicExceptionType {

    NOT_FOUND_AUTHORITY("NOT_FOUND_AUTHORITY", "존재하지 않는 권한입니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;


    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

