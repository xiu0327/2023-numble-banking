package numble.backend.account.dao;

import numble.backend.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from Account a join fetch a.member m where a.number= :number")
    Optional<Account> findAccountByNumber(@Param("number") String number);
}
