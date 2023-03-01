package numble.backend.friendship.application;

import lombok.RequiredArgsConstructor;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.common.exception.BusinessException;
import numble.backend.friendship.dao.FriendshipRepository;
import numble.backend.friendship.dao.FriendshipSelectRepository;
import numble.backend.member.dao.MemberRepository;
import numble.backend.member.dto.response.MemberBasicResponseDTO;
import numble.backend.friendship.dto.FriendDTO;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.exception.MemberExceptionType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipSelectRepository friendshipSelectRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public BasicResponseDTO<Long> addFriend(String ownerId, String friendId) {
        checkExistFriend(friendId);
        Friendship friendship = Friendship.builder()
                .ownerId(ownerId)
                .friendId(friendId).build();
        friendshipRepository.save(friendship);
        friendshipRepository.save(friendship.change());
        return new MemberBasicResponseDTO(
                friendship.getId(),
                friendId + "님을 친구로 추가했습니다."
        );
    }

    public void checkExistFriend(String friendId){
        memberRepository.findByUserId(friendId)
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    @Override
    public List<FriendDTO> findFriend(String ownerId, Pageable pageable) {
        return friendshipSelectRepository.findFriendList(ownerId, pageable).stream()
                .map(o -> new FriendDTO(o.getUserId(), o.getUsername()))
                .collect(Collectors.toList());
    }
}
