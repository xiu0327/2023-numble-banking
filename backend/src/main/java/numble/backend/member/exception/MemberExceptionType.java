package numble.backend.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numble.backend.common.exception.BasicExceptionType;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberExceptionType implements BasicExceptionType {

    NOT_FOUND_MEMBER("NOT_FOUND_MEMBER", "회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_MEMBER("DUPLICATE_MEMBER", "중복된 회원 입니다.", HttpStatus.BAD_REQUEST),
    NOT_EQUAL_PASSWORD("NOT_EQUAL_PASSWORD", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EQUAL_ID("NOT_EQUAL_ID", "아이디가 일치하지 않습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getErrorCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }
}
