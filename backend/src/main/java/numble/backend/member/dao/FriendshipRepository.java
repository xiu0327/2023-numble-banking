package numble.backend.member.dao;

import numble.backend.member.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("select f from Friendship f join fetch f.friend where f.ownerId= :ownerId")
    List<Friendship> findFriendshipByUserId(@Param("ownerId") String ownerId);

}
