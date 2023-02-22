package numble.backend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import numble.backend.account.entity.Account;

@Data
@Builder
@NoArgsConstructor
public class CreateAccountRequestDTO {
    private String userId;
    private String password;
    private int money;

    @Builder
    public CreateAccountRequestDTO(String userId, String password, int money) {
        this.userId = userId;
        this.password = password;
        this.money = money;
    }

    public Account toEntity(){
        return Account.builder()
                .password(password)
                .money(money).build();
    }
}
