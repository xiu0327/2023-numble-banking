package numble.backend.member.value;

import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.exception.MemberExceptionType;

@NoArgsConstructor
@Getter
public class Password {
    private String password;

    public Password(String password) {
        this.password = password;
    }

    public void encryptPassword(String password){
        this.password = password;
    }

    public void isSamePassword(String password){
        if (password.equals(this.password)){
            throw new BusinessException(MemberExceptionType.NOT_EQUAL_PASSWORD);
        }
    }
}
