package numble.backend.member.application;


import numble.backend.jwt.dto.TokenDTO;
import numble.backend.member.entity.Member;

/**
 * 회원 생성 및 인증/인가 관련 서비스
 * join : 회원가입
 * login : 로그인
 * reissue : 토큰 재발행
 */
public interface MemberAuthService {
    Long join(Member member);
    TokenDTO login(String userId, String password);
    TokenDTO reissue(String accessToken, String refreshToken);
}
