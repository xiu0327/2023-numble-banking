package numble.backend.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferResponseDTO {
    private String fromUserId;
    private String toUserId;
    private int money;
    private String message;

    @Builder
    public TransferResponseDTO(String fromUserId, String toUserId, int money) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.money = money;
        this.message = setMessageFormat(toUserId, money);
    }

    private String setMessageFormat(String toUserId, int money) {
        return toUserId + "님 에게 " + money + " 원을 송금하셨습니다.";
    }
}
