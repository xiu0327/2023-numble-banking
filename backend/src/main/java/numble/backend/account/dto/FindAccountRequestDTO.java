package numble.backend.account.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindAccountRequestDTO {
    private String accountNumber;
}
