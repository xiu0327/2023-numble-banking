package numble.backend.account.application;

import lombok.extern.slf4j.Slf4j;
import numble.backend.account.dto.CreateAccountRequestDTO;
import numble.backend.account.dto.TransferResponseDTO;
import numble.backend.account.entity.Account;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.application.MemberService;
import numble.backend.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class AccountServiceImplTest {

    @Autowired AccountService accountService;
    @Autowired
    MemberAuthService memberAuthService;
    @Autowired MemberService memberService;
    @Autowired
    EntityManager em;
    String password = "password123";
    String username = "사용자";
    int fromPresentMoney = 30000;
    int toPresentMoney = 10000;
    int remittanceMoney = 10000;

    private Member createMember(String userId) {
        Member member = Member.builder()
                .userId(userId)
                .password(password)
                .username(username).build();
        Long memberId = memberAuthService.join(member);
        return em.find(Member.class, memberId);
    }

    @Test
    void 계좌_생성(){
        // given
        Member member = createMember("aaa123");
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .userId(member.getUserId())
                .password("1234")
                .money(10000).build();
        // when
        Long accountId = accountService.create(request.getUserId(), request.getPassword(), request.getMoney()).getResource();

        // then
        Account account = em.find(Account.class, accountId);
        assertThat(account.getMember().getUserId()).isEqualTo(member.getUserId());
    }

    @Test
    void 계좌_이체_정상(){
        // given
        Member from = createMember("aaa123");
        Member to = createMember("bbb123");

        memberService.addFriend(from.getUserId(), to.getUserId());

        Long fromAccountId = accountService.create(from.getUserId(), "1234", fromPresentMoney).getResource();
        Long toAccountId = accountService.create(to.getUserId(), "1234", toPresentMoney).getResource();

        Account fromAccount = em.find(Account.class, fromAccountId);
        Account toAccount = em.find(Account.class, toAccountId);

        // when
        TransferResponseDTO response = accountService.transfer(fromAccount.getNumber(), toAccount.getNumber(), remittanceMoney);

        Account afterFromAccount = getAfterAccount(fromAccountId);
        Account afterToAccount = getAfterAccount(toAccountId);

        // then
        log.info(response.getMessage());
        assertThat(afterFromAccount.getMoney()).isEqualTo(fromPresentMoney - remittanceMoney);
        assertThat(afterToAccount.getMoney()).isEqualTo(toPresentMoney + remittanceMoney);
    }

    @Test
    void 계좌_이체_정상_실패(){     /* 친구가 아닌 사람과 거래할 때 */
        // given
        Member from = createMember("aaa123");
        Member to = createMember("bbb123");

        Long fromAccountId = accountService.create(from.getUserId(), "1234", fromPresentMoney).getResource();
        Long toAccountId = accountService.create(to.getUserId(), "1234", toPresentMoney).getResource();

        Account fromAccount = em.find(Account.class, fromAccountId);
        Account toAccount = em.find(Account.class, toAccountId);

        // when & then
        assertThrows(BusinessException.class,
                () -> accountService.transfer(fromAccount.getNumber(), toAccount.getNumber(), remittanceMoney));
    }

    @Test
    void 계좌_이체_동시_요청() throws InterruptedException {
        // given
        Member from = createMember("aaa123");
        Member to = createMember("bbb123");

        Long fromAccountId = accountService.create(from.getUserId(), "1234", fromPresentMoney).getResource();
        Long toAccountId = accountService.create(to.getUserId(), "1234", toPresentMoney).getResource();

        Account fromAccount = em.find(Account.class, fromAccountId);
        Account toAccount = em.find(Account.class, toAccountId);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // when
        for (int i = 0; i < 1000; i++){
            executorService.submit(() -> accountService.transfer(fromAccount.getNumber(), toAccount.getNumber(), remittanceMoney));
            executorService.submit(() -> accountService.transfer(toAccount.getNumber(), fromAccount.getNumber(), remittanceMoney));
        }

        executorService.shutdown();;
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Account afterFromAccount = getAfterAccount(fromAccountId);
        Account afterToAccount = getAfterAccount(toAccountId);

        // when & then
        log.info("afterFromAccount = {}", afterFromAccount.getMoney());
        log.info("afterToAccount = {}", afterToAccount.getMoney());
        assertThat(afterFromAccount.getMoney()).isEqualTo(fromPresentMoney);
        assertThat(afterToAccount.getMoney()).isEqualTo(toPresentMoney);

    }

    private Account getAfterAccount(Long accountId) {
        return em.createQuery("select a from Account a where a.id= :accountId", Account.class)
                .setParameter("accountId", accountId)
                .getResultList().stream().findAny().get();
    }

}