package numble.backend.account.dto;


import lombok.*;

@Data
@NoArgsConstructor
public class AccountDTO {
    private String userId;
    private String accountNumber;
    private int money;
    private String message;

    @Builder
    public AccountDTO(String userId, int money, String accountNumber) {
        this.userId = userId;
        this.money = money;
        this.accountNumber = accountNumber;
        this.message = setMessageFormat(userId);
    }

    private String setMessageFormat(String userId){
        return "현재 " + userId + " 님의 계좌엔 " + money + " 원 남았습니다.";
    }
}
