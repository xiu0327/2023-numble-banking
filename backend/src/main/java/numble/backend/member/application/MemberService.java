package numble.backend.member.application;

import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.member.dto.response.MemberDTO;

import java.util.List;

/**
 * addFriend : 친구 추가
 * findFriend : 친구 조회
 */
public interface MemberService {
    BasicResponseDTO<Long> addFriend(String ownerId, String friendId);
    List<MemberDTO> findFriend(String ownerId);
}
