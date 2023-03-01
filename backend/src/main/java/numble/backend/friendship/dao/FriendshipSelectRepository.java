package numble.backend.friendship.dao;

import numble.backend.member.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendshipSelectRepository {
    List<Member> findFriendList(String ownerId, Pageable pageable);
}
