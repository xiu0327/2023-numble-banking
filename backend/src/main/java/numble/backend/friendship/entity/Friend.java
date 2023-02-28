package numble.backend.friendship.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "friend")
@Getter
public class Friend {

    @Id
    @Column(name = "friend_user_id")
    private String friendId;

    @Column(name = "friend_name")
    private String friendName;

    @Column(name = "transaction")
    private int transaction;

    public Friend(String friendId, String friendName) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.transaction = 0;
    }

    public void increaseTransaction(){
        transaction++;
    }

    public void decreaseTransaction(){
        transaction--;
    }
}
