package numble.backend.account;

import numble.backend.account.application.AccountService;
import numble.backend.common.util.TestData;
import numble.backend.friendship.application.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AccountDatabaseInit {

    @Autowired AccountService accountService;
    @Autowired FriendshipService friendshipService;
    @Autowired TestData testData;

    @Test
    void init(){
        String fromID = testData.createMember("owner111");
        String toID = testData.createMember("friend111");
        friendshipService.addFriend(fromID, toID);

        String from = accountService.create(fromID, TestData.ACCOUNT_PASSWORD).getResource();
        String to = accountService.create(toID, TestData.ACCOUNT_PASSWORD).getResource();

        testData.settingMoney(from);
        testData.settingMoney(to);
    }

    @Test
    @Commit
    void friend_init(){
        String fromID = testData.createMember("owner111");

        for (int i = 0; i < 50; i++){
            String toID = testData.createMember("friend" + i);
            friendshipService.addFriend(fromID, toID);
        }
    }

}
