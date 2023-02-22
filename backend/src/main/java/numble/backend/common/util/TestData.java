package numble.backend.common.util;


import lombok.RequiredArgsConstructor;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.dto.request.JoinMemberRequestDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Profile("local")
@Component
@RequiredArgsConstructor
public class TestData {

    private final InitTestDataService initTestDataService;
    public static final String USER_A_ID = "aaa123";
    public static final String USER_B_ID = "bbb123";

    @PostConstruct
    private void init(){
        initTestDataService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitTestDataService{
        private final MemberAuthService memberAuthService;
        private final MemberRepository memberRepository;

        @Transactional
        public void init(){
            memberAuthService.join(JoinMemberRequestDTO.builder()
                    .userId("aaa123")
                    .password("password123")
                    .username("사용자_A").build().toEntity());

            memberAuthService.join(JoinMemberRequestDTO.builder()
                    .userId("bbb123")
                    .password("password123")
                    .username("사용자_B").build().toEntity());
        }
    }
}
