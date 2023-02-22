package numble.backend.account.dto;

import numble.backend.common.dto.BasicResponseDTO;

public class CreateAccountResponseDTO extends BasicResponseDTO<Long> {
    public CreateAccountResponseDTO(Long resource, String message) {
        super(resource, message);
    }
}
