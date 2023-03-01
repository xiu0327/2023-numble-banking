package numble.backend.friendship.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import numble.backend.friendship.entity.QFriendship;
import numble.backend.member.entity.Member;
import numble.backend.member.entity.QMember;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipSelectRepositoryImpl implements FriendshipSelectRepository{

    private final EntityManager em;

    @Override
    public List<Member> findFriendList(String ownerId, Pageable pageable) {
        QMember member = QMember.member;
        QFriendship fs = new QFriendship("fs");
        return new JPAQueryFactory(em)
                .select(member)
                .from(member)
                .join(fs)
                .on(fs.friendId.eq(member.userId))
                .where(fs.ownerId.eq(ownerId))
                .orderBy(fs.transaction.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
}
