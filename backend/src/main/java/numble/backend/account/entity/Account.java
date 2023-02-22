package numble.backend.account.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.member.entity.Member;

import javax.persistence.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "account")
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_number")
    private String number;

    @Column(name = "account_password")
    private String password;

    @Column(name = "money")
    private int money;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder
    public Account(String password, Member member, int money) {
        this.number = UUID.randomUUID().toString();
        this.password = password;
        this.member = member;
        this.money = money;
    }

    /* 비즈니스 로직 */
    public void updateMoney(int money){
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Member getMember() {
        return member;
    }

    public int getMoney() {
        return money;
    }
}
