package numble.backend.common.util;

import lombok.RequiredArgsConstructor;
import numble.backend.account.entity.Account;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class TestData {

    @Autowired
    MemberAuthService memberAuthService;
    @Autowired
    EntityManager em;
    public static final String PASSWORD = "password123";
    public static final String USERNAME = "사용자";

    public static final int FROM_PRESENT_MONEY = 20000;
    public static final int TO_PRESENT_MONEY = 10000;
    public static final int MONEY = 10000;
    public static final String ACCOUNT_PASSWORD = "1234";

    @Transactional
    public String createMember(String userId) {
        Member member = Member.builder()
                .userId(userId)
                .password(PASSWORD)
                .username(USERNAME).build();
        Long memberId = memberAuthService.join(member);
        return em.find(Member.class, memberId).getUserId();
    }

    @Transactional
    public void settingMoney(String accountNumber){
        Account account = getAccount(accountNumber);
        account.deposit(FROM_PRESENT_MONEY);
    }

    @Transactional(readOnly = true)
    public Account findAccountByNumber(String accountNumber){
        return getAccount(accountNumber);
    }

    private Account getAccount(String accountNumber) {
        return em.createQuery("select a from Account a where a.accountNumber= :accountNumber", Account.class)
                .setParameter("accountNumber", accountNumber)
                .getResultList().stream().findAny().get();
    }

    public Friendship findFriendshipByOwnerId(String ownerId){
        return em.createQuery("select f from Friendship f where f.ownerId= :ownerId", Friendship.class)
                .setParameter("ownerId", ownerId)
                .getResultList().stream().findAny().get();
    }

}
