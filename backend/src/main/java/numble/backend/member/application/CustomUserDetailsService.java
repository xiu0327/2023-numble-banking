package numble.backend.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.backend.authority.entity.Authority;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) {
        log.debug("CustomUserDetailsService -> userId = {}", userId);
        return memberRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member getMember(String userId){
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    private UserDetails createUserDetails(Member member){
        List<SimpleGrantedAuthority> authorities = member.getAuthorities().stream()
                .map(Authority::getAuthorityName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.forEach(o -> log.debug("authorities -> {}", o.getAuthority()));

        return new User(member.getUserId(), member.getPassword(), authorities);
    }
}

