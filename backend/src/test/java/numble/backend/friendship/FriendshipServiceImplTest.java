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
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
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

    @Test
    void 친구_목록_조회_쿼리_테스트(){
        // given
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");
        friendshipService.addFriend(ownerId, friendId);

        String friendId2 = testData.createMember("friend222");
        friendshipService.addFriend(ownerId, friendId2);

        List<String> friends = new ArrayList<>();
        friends.add(friendId);
        friends.add(friendId2);

        String sql = "select distinct m from Member m inner join Friendship f on m.userId = f.friendId where f.ownerId=: ownerId";

        List<Member> ownerId1 = em.createQuery(sql, Member.class)
                .setParameter("ownerId", ownerId)
                .getResultList();

        for (Member member : ownerId1) {
            log.info("member = {}", member.getUserId());
            Assertions.assertThat(member.getUserId()).isIn(friends);
        }
    }

    @Test
    void 친구_목록_생성_시간_테스트() throws InterruptedException {
        // given
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");

        /* 연관매핑 관계를 맺었을 때 - 생성 */
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++){
            selectMember(ownerId);
            selectMember(friendId);
            Thread.sleep(500); // save 시간
        }
        log.info("연관 매핑 시간 = {}", System.currentTimeMillis() - start1);

        /* 연관 매핑을 맺지 않았을 때 - 생성 */
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++){
            Friendship friendship = Friendship.builder()
                    .ownerId(ownerId)
                    .friendId(friendId).build();
            Thread.sleep(500); // save 시간
        }
        log.info("연관 매핑 하지 않은 시간 = {}", System.currentTimeMillis() - start2);
    }
    Member selectMember(String id){
        return em.createQuery("select m from Member m where m.userId= :id", Member.class)
                .setParameter("id", id)
                .getResultList().stream().findAny().get();
    }
}