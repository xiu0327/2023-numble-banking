package numble.backend.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.backend.authority.dao.AuthorityRepository;
import numble.backend.authority.entity.Authority;
import numble.backend.common.exception.BusinessException;
import numble.backend.jwt.dao.RefreshTokenRepository;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.jwt.entity.CustomUserIdPasswordAuthToken;
import numble.backend.jwt.entity.RefreshToken;
import numble.backend.jwt.exception.JwtExceptionType;
import numble.backend.jwt.provider.TokenProvider;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import numble.backend.member.value.MemberRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT를 사용한 로그인 구현체
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtMemberAuthServiceImpl implements MemberAuthService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    @Override
    public Long join(Member member) {
        toIdentifyDuplicateMember(member.getUserId());
        member.encryptPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(addAuthority(member)).getId();
    }

    private void toIdentifyDuplicateMember(String userId) {
        if(memberRepository.findByUserId(userId).isPresent()){
            throw new BusinessException(MemberExceptionType.DUPLICATE_MEMBER);
        }
    }

    private Member addAuthority(Member member) {
        Authority authority = new Authority(MemberRole.ROLE_USER);
        authorityRepository.save(authority);
        member.addAuthority(authority);
        return member;
    }

    @Override
    @Transactional
    public TokenDTO login(String userId, String password) {
        // STEP 1. 인증 객체 생성
        CustomUserIdPasswordAuthToken customEmailPasswordAuthToken = new CustomUserIdPasswordAuthToken(userId, password);
        // STEP 2. 아이디, 비밀번호로 인증 -> 해당 인증 객체가 유효한지 판단
        Authentication authenticate = authenticationManager.authenticate(customEmailPasswordAuthToken);
        // STEP 3. 회원 조회
        String findUserId = authenticate.getName();
        Member member = customUserDetailsService.getMember(findUserId);
        // STEP 4. accessToken, refreshToken 생성
        String accessToken = tokenProvider.createAccessToken(findUserId, member.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(findUserId, member.getAuthorities());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .key(findUserId)
                        .value(refreshToken)
                        .build());
        return tokenProvider.createTokenDTO(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenDTO reissue(String accessToken, String refreshToken) {
        int refreshTokenFlag = tokenProvider.validateToken(accessToken);

        checkRefreshTokenValidation(refreshTokenFlag);

        RefreshToken findRefreshToken = checkRefreshTokenState(accessToken, refreshToken);

        String userId = tokenProvider.getUserIdByToken(accessToken);
        Member member = customUserDetailsService.getMember(userId);

        String newAccessToken = tokenProvider.createAccessToken(userId, member.getAuthorities());
        String newRefreshToken = tokenProvider.createRefreshToken(userId, member.getAuthorities());
        TokenDTO tokenDTO = tokenProvider.createTokenDTO(newAccessToken, newRefreshToken);

        log.debug("refresh Origin = {}", refreshToken);
        log.debug("refresh New = {}", newRefreshToken);
        findRefreshToken.updateValue(newRefreshToken);
        return tokenDTO;
    }

    private RefreshToken checkRefreshTokenState(String originAccessToken, String originRefreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        log.debug("Authentication = {}", authentication);

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new BusinessException(MemberExceptionType.LOGOUT_MEMBER));

        if(!refreshToken.getValue().equals(originRefreshToken))
            throw new BusinessException(JwtExceptionType.BAD_TOKEN);
        return refreshToken;
    }

    private void checkRefreshTokenValidation(int refreshTokenFlag) {
        if (refreshTokenFlag == -1) {
            throw new BusinessException(JwtExceptionType.BAD_TOKEN);
        } else if (refreshTokenFlag == 2) {
            throw new BusinessException(JwtExceptionType.REFRESH_TOKEN_EXPIRED);
        }
    }
}
