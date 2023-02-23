package numble.backend.friendship;

import lombok.extern.slf4j.Slf4j;
import numble.backend.common.util.TestData;
import numble.backend.friendship.application.FriendshipService;
import numble.backend.friendship.dto.FriendDTO;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@Transactional
class FriendshipServiceImplTest {

    @Autowired FriendshipService friendshipService;
    @Autowired EntityManager em;
    @Autowired TestData testData;

    @Test
    void 친구_추가() {
        // given
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");

        // when
        log.info("===== add Friend start =====");
        friendshipService.addFriend(ownerId, friendId);
        log.info("===== add Friend end =====");

        // then
        List<String> owner = findFriendships(ownerId);
        List<String> friend = findFriendships(friendId);

        // then
        Assertions.assertThat(friendId).isIn(owner);
        Assertions.assertThat(ownerId).isIn(friend);
    }

    private List<String> findFriendships(String ownerId) {
        return em.createQuery("select f from Friendship f where f.ownerId= :ownerId", Friendship.class)
                .setParameter("ownerId", ownerId)
                .getResultList().stream()
                .map(Friendship::getFriendId).collect(Collectors.toList());
    }

    @Test
    void 친구_목록_조회() {
        // given
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");
        friendshipService.addFriend(ownerId, friendId);

        // when
        log.info("===== find Friend List start =====");
        Pageable pageable = PageRequest.of(0, 20);
        List<FriendDTO> result = friendshipService.findFriend(ownerId, pageable);
        log.info("===== find Friend List end =====");

        // then
        log.info(result.toString());
        Assertions.assertThat(result.stream()
                        .anyMatch(o -> o.getUserId().equals(friendId))).isTrue();
    }
}