package numble.backend.account.dao;

import numble.backend.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from Account a join fetch a.owner m where a.accountNumber= :accountNumber")
    Optional<Account> findAccountByNumber(@Param("accountNumber") String accountNumber);
}
