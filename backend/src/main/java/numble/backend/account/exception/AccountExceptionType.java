package numble.backend.account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numble.backend.common.exception.BasicExceptionType;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountExceptionType implements BasicExceptionType {
    NOT_FRIEND("NOT_FRIEND", "해당 사용자와 거래할 수 없습니다.", HttpStatus.BAD_REQUEST),
    LACK_MONEY("LACK_MONEY", "잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_ACCOUNT("NOT_FOUND_ACCOUNT", "계좌를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    FAIL_NOTIFY("FAIL_NOTIFY", "알람을 보내지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_EQUAL_PASSWORD("NOT_EQUAL_PASSWORD", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST)
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
