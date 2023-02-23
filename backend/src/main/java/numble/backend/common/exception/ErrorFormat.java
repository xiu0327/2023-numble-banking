package numble.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorFormat {

    private String code;
    private String message;

    static ErrorFormat create(BasicExceptionType baseExceptionType){
        return new ErrorFormat(baseExceptionType.getErrorCode(), baseExceptionType.getMessage());
    }
}
