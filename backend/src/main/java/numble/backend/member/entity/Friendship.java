package numble.backend.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "friendship")
@Getter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @Column(name = "transaction")
    private Integer transaction;

    @Column(name = "owner_id")
    private String ownerId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member friend;

    @Builder
    public Friendship(Integer transaction, String ownerId, Member friend) {
        this.transaction = transaction;
        this.ownerId = ownerId;
        this.friend = friend;
    }

    /* 비즈니스 로직 */

    public void increaseTransaction(){
        this.transaction++;
    }

    public void decreaseTransaction(){
        this.transaction--;
    }

    public Friendship change(Member owner){
        return Friendship.builder()
                .ownerId(friend.getUserId())
                .friend(owner)
                .transaction(0).build();
    }

}
