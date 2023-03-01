package numble.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum InternalServerExceptionType implements BasicExceptionType{
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR","서버 내부에 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
