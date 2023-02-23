package numble.backend.account.dto;

import numble.backend.common.dto.BasicResponseDTO;

public class CreateAccountResponseDTO extends BasicResponseDTO<String> {
    public CreateAccountResponseDTO(String resource, String message) {
        super(resource, message);
    }
}
