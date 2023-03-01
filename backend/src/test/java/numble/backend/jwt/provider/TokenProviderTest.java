package numble.backend.jwt.provider;

import lombok.extern.slf4j.Slf4j;
import numble.backend.authority.entity.Authority;
import numble.backend.member.value.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Test
    void 토큰_발행() {
        // given
        String userId = "userid12334";
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(MemberRole.ROLE_USER));
        long tokenValid = 100000;

        // when
        String token = tokenProvider.createToken(userId, authorities, tokenValid);

        // then
        log.info("token = {}", token);
    }

    @Test
    void 토큰_파싱() {
        // given
        String userId = "userid12334";
        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority(MemberRole.ROLE_USER));
        long tokenValid = 100000;
        String token = tokenProvider.createToken(userId, authorities, tokenValid);

        // when
        String getUserId = tokenProvider.getUserIdByToken(token);

        // then
        log.info(getUserId);
        assertThat(getUserId).isEqualTo(userId);
    }
}