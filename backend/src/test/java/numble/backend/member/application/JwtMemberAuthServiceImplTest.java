package numble.backend.member.application;

import lombok.extern.slf4j.Slf4j;
import numble.backend.common.exception.BusinessException;
import numble.backend.common.util.TestData;
import numble.backend.jwt.dto.TokenDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class JwtMemberAuthServiceImplTest {

    @Autowired MemberAuthService memberAuthService;
    @Autowired TestData testData;

    @Test
    void 로그인(){
        String userId = testData.createMember("userid111");
        TokenDTO token = memberAuthService.login(userId, TestData.PASSWORD);
        log.info("token = {}", token);
        assertThat(token).isNotNull();
        assertThat(token.getGrantType()).isEqualTo("Bearer");
    }

    @Test
    void 로그인_실패(){ /* 비밀번호 불일치 */
        String userId = testData.createMember("userid111");
        assertThrows(BusinessException.class, () -> {
            try{
                memberAuthService.login(userId, "");
            }catch (BusinessException e){
                log.info(e.getMessage());
                assertThat(e.getBasicExceptionType().getErrorCode()).isEqualTo("NOT_EQUAL_PASSWORD");
                throw e;
            }
        });
    }

    @Test
    void 토큰_재발행(){
        String userId = testData.createMember("userid111");
        TokenDTO token = memberAuthService.login(userId, TestData.PASSWORD);
        TokenDTO reissue = memberAuthService.reissue(token.getAccessToken(), token.getRefreshToken());
        // 유효 시간이 지나지 않았기 때문에 동일함
        // 유효 시간이 지났다면 Token 다름
        assertThat(token.getAccessToken()).isEqualTo(reissue.getAccessToken());
    }
}