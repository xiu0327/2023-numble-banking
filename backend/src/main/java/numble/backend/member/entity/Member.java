package numble.backend.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.account.entity.Account;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.exception.MemberExceptionType;
import numble.backend.member.value.Password;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Embedded
    private Password password;

    @Column(name = "username", length = 30, nullable = false)
    private String username;

    @Transient
    private boolean activate;

    @OneToMany(mappedBy = "owner")
    private final List<Account> accounts = new ArrayList<>();

    @Builder
    public Member(String userId, String password, String username) {
        this.userId = userId;
        this.password = new Password(password).encryptPassword();
        this.username = username;
        this.activate = false;
    }

    /* 비즈니스 로직 */

    public void login(String userId, String password){
        isSameId(userId);
        this.password.isSamePassword(password);
        this.activate = true;
    }

    public boolean isActivate(){
        return this.activate;
    }

    public List<Account> getAccounts(){
        return this.accounts;
    }

    public void isSameId(String id){
        if (!id.equals(this.userId)){
            throw new BusinessException(MemberExceptionType.NOT_EQUAL_ID);
        }
    }

    /* 연관관계 편의 메서드 */

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    public void remove(Account account){
        this.accounts.remove(account);
    }

    /* get */

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
