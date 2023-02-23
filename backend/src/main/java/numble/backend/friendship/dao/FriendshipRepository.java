package numble.backend.friendship.dao;

import numble.backend.friendship.entity.Friendship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("select f.friendId from Friendship f where f.ownerId= :ownerId")
    List<String> findByOwnerId(@Param("ownerId") String ownerId, Pageable pageable);

    @Query("select f.friendId from Friendship f where f.ownerId= :ownerId")
    List<String> findAllByOwnerId(@Param("ownerId") String ownerId);
}
