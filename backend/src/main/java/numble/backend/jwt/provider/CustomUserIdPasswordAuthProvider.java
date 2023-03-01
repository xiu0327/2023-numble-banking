package numble.backend.jwt.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.backend.common.exception.BusinessException;
import numble.backend.jwt.entity.CustomUserIdPasswordAuthToken;
import numble.backend.member.application.CustomUserDetailsService;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserIdPasswordAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = null;
        try{
            user = retrieveUser(authentication.getName());
        } catch (BusinessException e){
            throw e;
        }
        Object principalToReturn = user;
        CustomUserIdPasswordAuthToken result = new CustomUserIdPasswordAuthToken( // 인증 객체 생성
                principalToReturn,
                authentication.getCredentials(),
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        additionalAuthenticationChecks(user, result); // 비밀번호 추가 검증
        result.setDetails(authentication.getDetails());
        return result;
    }

    private void additionalAuthenticationChecks(UserDetails user, CustomUserIdPasswordAuthToken authentication) throws BusinessException{
        log.debug("additionalAuthenticationChecks authentication = {}", authentication);
        if (authentication.getCredentials() == null){
            log.debug("additionalAuthenticationChecks is null !");
            throw new BusinessException(MemberExceptionType.NOT_EQUAL_PASSWORD);
        }
        String password = authentication.getCredentials().toString();
        log.debug("authentication.password = {}", password);

        if(!this.passwordEncoder.matches(password, user.getPassword()))
            throw new BusinessException(MemberExceptionType.NOT_EQUAL_PASSWORD);

    }

    private UserDetails retrieveUser(String name) throws BusinessException{
        try {
            UserDetails loadedUser = customUserDetailsService.loadUserByUsername(name);
            if (loadedUser == null)
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            return loadedUser;
        } catch (BusinessException e){
            log.debug("error in retrieveUser = {}", e.getMessage());
            throw e;
        } catch (Exception e){
            throw new InternalAuthenticationServiceException("내부 인증 로직 중 알 수 없는 오류가 발생하였습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 인증 객체가 CustomEmailPasswordAuthToken.class 라면 CustomEmailPasswordAuthProvider 사용
        return authentication.equals(CustomUserIdPasswordAuthToken.class);
    }
}
