package numble.backend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private String inputPassword;
    private int money;
}
