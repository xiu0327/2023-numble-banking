package numble.backend.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.account.entity.Account;
import numble.backend.authority.entity.Authority;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.exception.MemberExceptionType;
import numble.backend.member.value.MemberRole;
import numble.backend.member.value.Password;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    @Getter
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    @Getter
    private String userId;

    @Embedded
    private Password password;

    @Column(name = "username", length = 30, nullable = false)
    @Getter
    private String username;

    @Transient
    private boolean activate;

    @Column(name = "role")
    @Getter
    private MemberRole role;

    @OneToMany(mappedBy = "owner")
    @Getter
    private final List<Account> accounts = new ArrayList<>();

    @Getter
    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name="member_id",referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name",referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities = new HashSet<>();

    @Builder
    public Member(String userId, String password, String username) {
        this.userId = userId;
        this.password = new Password(password);
        this.username = username;
        this.activate = false;
        this.role = MemberRole.ROLE_USER;
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

    public void encryptPassword(String password){
        this.password.encryptPassword(password);
    }

    /* 연관관계 편의 메서드 */

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    public void remove(Account account){
        this.accounts.remove(account);
    }

    public void addAuthority(Authority authority){
        this.authorities.add(authority);
    }

    public String getPassword() {
        return password.getPassword();
    }
}
