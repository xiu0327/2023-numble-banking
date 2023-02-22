package numble.backend.account.dto;

import numble.backend.common.dto.BasicResponseDTO;

public class AccountBasicResponseDTO extends BasicResponseDTO<Boolean> {
    public AccountBasicResponseDTO(Boolean resource, String message) {
        super(resource, message);
    }
}
