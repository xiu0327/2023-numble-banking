package numble.backend.member.dto.response;

import numble.backend.common.dto.BasicResponseDTO;

public class MemberBasicResponseDTO extends BasicResponseDTO<Long> {
    public MemberBasicResponseDTO(Long resource, String message) {
        super(resource, message);
    }
}
