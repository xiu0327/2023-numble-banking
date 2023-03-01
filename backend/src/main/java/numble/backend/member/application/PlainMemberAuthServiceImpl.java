package numble.backend.member.application;

import lombok.RequiredArgsConstructor;
import numble.backend.common.exception.BusinessException;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * jwt 을 사용하지 않은 순수 인증/인가 로직
 */

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlainMemberAuthServiceImpl implements MemberAuthService{

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long join(Member member) {
        toIdentifyDuplicateMember(member.getUserId());
        memberRepository.save(member);
        return member.getId();
    }

    private void toIdentifyDuplicateMember(String userId) {
        if(memberRepository.findByUserId(userId).isPresent()){
            throw new BusinessException(MemberExceptionType.DUPLICATE_MEMBER);
        }
    }

    @Override
    public TokenDTO login(String userId, String password) {
        return null;
    }

    @Override
    public TokenDTO reissue(String accessToken, String refreshToken) {
        return null;
    }
}
