package numble.backend.friendship.application;

import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.friendship.dto.FriendDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * addFriend : 친구 추가
 * findFriend : 친구 조회
 */
public interface FriendshipService {
    BasicResponseDTO<Long> addFriend(String ownerId, String friendId);
    List<FriendDTO> findFriend(String ownerId, Pageable pageable);
}
