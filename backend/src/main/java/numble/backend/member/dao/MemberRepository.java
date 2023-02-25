package numble.backend.member.dao;

import numble.backend.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);
    @Query("select m from Member m join fetch m.accounts where m.userId= :userId")
    Optional<Member> findFetchJoinByUserId(@Param("userId") String userId);
}
