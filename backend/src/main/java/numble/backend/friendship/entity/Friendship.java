package numble.backend.friendship.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.account.exception.AccountExceptionType;
import numble.backend.common.exception.BusinessException;
import numble.backend.friendship.exception.FriendExceptionType;
import numble.backend.friendship.value.Transaction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "friendship")
@Getter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @Embedded
    private Transaction transaction;

    @Column(name = "owner_id")
    private String ownerId;


    @Column(name = "friend_id")
    private String friendId;

    @Builder
    public Friendship(String ownerId, String friendId) {
        this.ownerId = ownerId;
        this.friendId = friendId;
        this.transaction = new Transaction();
    }

    /* 비즈니스 로직 */
    public Friendship change(){
        return Friendship.builder()
                .ownerId(friendId)
                .friendId(ownerId).build();
    }

    public void hasFriend(List<String> friends){
        if (friends.size() < 1){
            throw new BusinessException(FriendExceptionType.NOT_FOUND_FRIEND);
        }
    }
}
