package numble.backend.account.application;

import lombok.extern.slf4j.Slf4j;
import numble.backend.account.dto.CreateAccountRequestDTO;
import numble.backend.account.dto.TransferResponseDTO;
import numble.backend.account.entity.Account;
import numble.backend.common.exception.BusinessException;
import numble.backend.common.util.TestData;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.application.MemberAuthService;
import numble.backend.friendship.application.FriendshipService;
import numble.backend.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class AccountServiceImplTest {

    @Autowired AccountService accountService;
    @Autowired FriendshipService friendshipService;
    @Autowired TestData testData;

    @Test
    void 계좌_생성(){
        // given
        String userId = testData.createMember("aaa123");
        // when
        String accountNumber = accountService.create(userId, TestData.ACCOUNT_PASSWORD).getResource();

        // then
        Account account = testData.findAccountByNumber(accountNumber);
        assertThat(account.getOwner().getUserId()).isEqualTo(userId);
    }

    @Test
    void 계좌_이체_정상(){
        // given
        String fromID = testData.createMember("owner111");
        String toID = testData.createMember("friend111");
        friendshipService.addFriend(fromID, toID);

        String from = accountService.create(fromID,TestData.ACCOUNT_PASSWORD).getResource();
        String to = accountService.create(toID, TestData.ACCOUNT_PASSWORD).getResource();

        testData.settingMoney(from);

        // when
        log.info("====== transfer start =====");
        TransferResponseDTO response = accountService.transfer(from, to, TestData.ACCOUNT_PASSWORD, TestData.MONEY);

        Account fromAccount = testData.findAccountByNumber(from);
        Account toAccount = testData.findAccountByNumber(to);
        Friendship friendship = testData.findFriendshipByOwnerId(fromID);

        // then
        log.info(response.getMessage());
        assertThat(fromAccount.getAmount()).isEqualTo(TestData.FROM_PRESENT_MONEY - TestData.MONEY);
        assertThat(toAccount.getAmount()).isEqualTo(TestData.MONEY);
        assertThat(friendship.getTransaction()).isEqualTo(1);
    }

    @Test
    void 계좌_이체_정상_실패_not_friend(){     /* 친구가 아닌 사람과 거래할 때 */
        // given
        String fromID = testData.createMember("owner111");
        String toID = testData.createMember("friend111");

        String from = accountService.create(fromID, TestData.ACCOUNT_PASSWORD).getResource();
        String to = accountService.create(toID, TestData.ACCOUNT_PASSWORD).getResource();

        testData.settingMoney(from);

        int before = testData.findAccountByNumber(from).getAmount();

        // when & then
        assertThrows(BusinessException.class,
                () -> {
                    try{
                        accountService.transfer(from, to, TestData.ACCOUNT_PASSWORD, TestData.MONEY);
                    }catch (BusinessException e){
                        log.info(e.getMessage()); // 해당 사용자와 거래할 수 없습니다.
                        throw e;
                    }
                });
        // 트랜잭션 원자성이 보장되었는지 확인, 보장되었다면 돈이 그대로 있어야 함
        int after = testData.findAccountByNumber(from).getAmount();
        assertThat(before).isEqualTo(after);
    }

    @Test
    void 계좌_이체_정상_실패_lack_money(){     /* 통장에 잔고가 부족할 때 */
        // given
        String fromID = testData.createMember("owner111");
        String toID = testData.createMember("friend111");
        friendshipService.addFriend(fromID, toID);

        String from = accountService.create(fromID, TestData.ACCOUNT_PASSWORD).getResource();
        String to = accountService.create(toID, TestData.ACCOUNT_PASSWORD).getResource();

        int before = testData.findAccountByNumber(from).getAmount();

        // when & then
        assertThrows(BusinessException.class,
                () -> {
                    try{
                        accountService.transfer(from, to, TestData.ACCOUNT_PASSWORD, TestData.MONEY);
                    }catch (BusinessException e){
                        log.info(e.getMessage()); // 잔액이 부족합니다.
                        throw e;
                    }
                });
        // 트랜잭션 원자성이 보장되었는지 확인, 보장되었다면 돈이 그대로 있어야 함
        int after = testData.findAccountByNumber(from).getAmount();
        assertThat(before).isEqualTo(after);
    }

    @Test
    void 계좌_이체_동시성() throws InterruptedException {
        // given
        String clientA = testData.createMember("owner111");
        String accountA = accountService.create(clientA, TestData.ACCOUNT_PASSWORD).getResource();
        testData.settingMoney(accountA); // clientA 계좌 잔액 : 20000원

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // when : 같은 스레드 내에서 동시에 출금
        Account target = testData.findAccountByNumber(accountA);
        executorService.submit(() -> {
            target.withdrawal(10000);
        });
        executorService.submit(() -> {
            target.withdrawal(15000);
        });

        executorService.shutdown();;
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        int fromResult = testData.findAccountByNumber(accountA).getAmount();

        // when & then
        log.info("afterFromAccount = {}", fromResult);
        assertThat(fromResult).isEqualTo(10000);
    }

    @Test
    void 계좌_이체_동시성_입금() throws InterruptedException {
        // given
        String clientA = testData.createMember("owner111");
        String accountA = accountService.create(clientA, TestData.ACCOUNT_PASSWORD).getResource();
        testData.settingMoney(accountA); // clientA 계좌 잔액 : 20000원

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // when : 같은 스레드 내에서 동시에 입금
        Account target = testData.findAccountByNumber(accountA);
        executorService.submit(() -> {
            target.deposit(10000);
        });
        executorService.submit(() -> {
            target.deposit(10000);
        });

        executorService.shutdown();;
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        int fromResult = testData.findAccountByNumber(accountA).getAmount();

        // when & then
        log.info("afterFromAccount = {}", fromResult);
        assertThat(fromResult).isEqualTo(40000);
    }
}