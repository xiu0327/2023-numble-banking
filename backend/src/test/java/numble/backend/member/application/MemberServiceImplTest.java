package numble.backend.member.application;

import lombok.extern.slf4j.Slf4j;
import numble.backend.member.dto.response.MemberDTO;
import numble.backend.member.entity.Friendship;
import numble.backend.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired MemberAuthService memberAuthService;
    @Autowired EntityManager em;
    String password = "password123";
    String username = "사용자";

    @Test
    void 친구_추가() {
        // given
        String ownerId = "aaa123";
        String friendId = "bbb123";

        Member member = createMember(ownerId);
        Member friend = createMember(friendId);

        log.info("===== add Friend start =====");
        // when
        memberService.addFriend(
                member.getUserId(),
                friend.getUserId());
        log.info("===== add Friend end =====");

        List<Member> ownerList = findFriendships(ownerId);
        List<Member> targetList = findFriendships(friendId);

        // then
        Assertions.assertThat(friend).isIn(ownerList);
        Assertions.assertThat(member).isIn(targetList);
    }

    private List<Member> findFriendships(String ownerId) {
        return em.createQuery("select f from Friendship f join fetch f.friend where f.ownerId= :ownerId", Friendship.class)
                .setParameter("ownerId", ownerId)
                .getResultList().stream()
                .map(Friendship::getFriend).collect(Collectors.toList());
    }

    private Member createMember(String userId) {
        Member member = Member.builder()
                .userId(userId)
                .password(password)
                .username(username).build();
        Long memberId = memberAuthService.join(member);
        return em.find(Member.class, memberId);
    }

    @Test
    void 친구_목록_조회() {
        // given
        String ownerId = "aaa123";
        String friendId = "bbb123";

        createMember(ownerId);
        Member friend = createMember(friendId);

        memberService.addFriend(ownerId, friendId);

        // when
        log.info("===== find Friend List start =====");
        List<MemberDTO> result = memberService.findFriend(ownerId);
        log.info("===== find Friend List end =====");

        // then
        log.info(result.toString());
        Assertions.assertThat(result.stream()
                        .anyMatch(o -> o.getUserId().equals(friend.getUserId()))).isTrue();
    }
}