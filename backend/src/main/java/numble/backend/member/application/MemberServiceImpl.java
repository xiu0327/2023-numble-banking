package numble.backend.member.application;

import lombok.RequiredArgsConstructor;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.dao.FriendshipRepository;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.dto.response.MemberBasicResponseDTO;
import numble.backend.member.dto.response.MemberDTO;
import numble.backend.member.entity.Friendship;
import numble.backend.member.entity.Member;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public BasicResponseDTO<Long> addFriend(String ownerId, String friendId) {
        Member owner = getMember(ownerId);
        Member friend = getMember(friendId);
        Friendship friendship = Friendship.builder()
                .ownerId(ownerId)
                .friend(friend)
                .transaction(0).build();
        friendshipRepository.save(friendship);
        friendshipRepository.save(friendship.change(owner));
        return new MemberBasicResponseDTO(
                friendship.getId(),
                friendId + "님을 친구로 추가했습니다."
        );
    }

    private Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    @Override
    public List<MemberDTO> findFriend(String ownerId) {
        return friendshipRepository.findFriendshipByUserId(ownerId).stream()
                .map(o -> new MemberDTO(o.getFriend().getUserId(), o.getFriend().getUsername()))
                .collect(Collectors.toList());
    }
}
