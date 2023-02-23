package numble.backend.member.dao;

import numble.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.userId in (:ids)")
    List<Member> findMemberByUserId(@Param("ids") List<String> ids);
    Optional<Member> findByUserId(String userId);
}
