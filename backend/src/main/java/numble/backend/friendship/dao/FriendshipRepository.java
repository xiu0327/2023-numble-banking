package numble.backend.friendship.dao;

import numble.backend.friendship.entity.Friendship;
import numble.backend.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("select distinct m from Member m inner join Friendship f on m.userId = f.friendId where f.ownerId=: ownerId")
    List<Member> findFriendList(@Param("ownerId") String ownerId, Pageable pageable);

    @Query("select f.friendId from Friendship f where f.ownerId= :ownerId")
    List<String> findAllByOwnerId(@Param("ownerId") String ownerId);
}
