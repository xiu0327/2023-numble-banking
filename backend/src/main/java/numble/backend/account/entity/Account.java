package numble.backend.account.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.account.exception.AccountExceptionType;
import numble.backend.common.exception.BusinessException;
import numble.backend.friendship.entity.Friendship;
import numble.backend.member.entity.Member;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account")
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Version
    private Integer version;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_password")
    private String accountPassword;

    @Column(name = "amount")
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member owner;

    @Builder
    public Account(String accountPassword, Member owner) {
        this.accountNumber = createAccountNumber();
        this.accountPassword = accountPassword;
        this.amount = 0;
        this.owner = owner;
    }

    public Account(String accountPassword, Member owner, int amount) {
        this.accountNumber = createAccountNumber();
        this.accountPassword = accountPassword;
        this.amount = amount;
        this.owner = owner;
    }

    /* 비즈니스 로직 */
    public void deposit(int money){
        this.amount += money;
    }

    public void withdrawal(int money){
        isPossible(money);
        this.amount -= money;
    }

    public void checkAccountPassword(String accountPassword){
        if (!accountPassword.equals(this.accountPassword)){
            throw new BusinessException(AccountExceptionType.NOT_EQUAL_PASSWORD);
        }
    }

    public void isPossible(int money){
        if (this.amount < money){
            throw new BusinessException(AccountExceptionType.LACK_MONEY);
        }
    }

    public String createAccountNumber(){
        return UUID.randomUUID().toString();
    }

    /**
     * 친구인지 아닌지 확인
     * @param friends 돈을 입금하는 쪽의 친구 목록
     */
    public void isFriend(List<String> friends){
        boolean result = friends.stream().anyMatch(f -> f.equals(owner.getUserId()));
        if (!result){
            throw new BusinessException(AccountExceptionType.NOT_FRIEND);
        }
    }

    public Friendship findFriend(List<Friendship> friendships){
        Friendship friendship = friendships.stream()
                .filter(f -> f.getFriendId().equals(owner.getUserId()))
                .findAny()
                .orElseThrow(() -> new BusinessException(AccountExceptionType.NOT_FRIEND));
        return friendship;
    }

    /* get */

    public String getAccountNumber() {
        return accountNumber;
    }

    public Member getOwner() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }
}
