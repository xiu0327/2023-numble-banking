package numble.backend.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.account.entity.Account;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", length = 30, nullable = false)
    private String username;


    @OneToMany(mappedBy = "member")
    private final List<Account> accounts = new ArrayList<>();

    @Builder
    public Member(String userId, String password, String username) {
        this.userId = userId;
        this.password = password;
        this.username = username;
    }

    /* 비즈니스 로직 */

    /**
     * 비밀번호 암호화
     * @param password 암호화된 비밀번호
     */
    public void encryptPassword(String password){
        this.password = password;
    }
    public void addAccount(Account account){
        this.accounts.add(account);
    }

}
