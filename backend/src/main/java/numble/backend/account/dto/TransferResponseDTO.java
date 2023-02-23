package numble.backend.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferResponseDTO {
    private String message;

    public TransferResponseDTO(String message) {
        this.message = message;
    }

}
