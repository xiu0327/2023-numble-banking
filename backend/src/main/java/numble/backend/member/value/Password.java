package numble.backend.member.value;

import lombok.NoArgsConstructor;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.exception.MemberExceptionType;

@NoArgsConstructor
public class Password {
    private String password;

    public Password(String password) {
        this.password = password;
    }

    public Password encryptPassword(){
        return this;
    }

    public void isSamePassword(String password){
        if (password.equals(this.password)){
            throw new BusinessException(MemberExceptionType.NOT_EQUAL_PASSWORD);
        }
    }
}
