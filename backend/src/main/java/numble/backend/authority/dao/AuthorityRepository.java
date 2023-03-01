package numble.backend.authority.dao;

import numble.backend.authority.entity.Authority;
import numble.backend.member.value.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Optional<Authority> findByAuthorityName(MemberRole memberRole);
}
