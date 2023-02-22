package numble.backend.common.dto;

import lombok.Getter;

@Getter
public abstract class BasicResponseDTO<T> {
    private T resource;
    private String message;


    public BasicResponseDTO() {
    }

    public BasicResponseDTO(T resource, String message) {
        this.resource = resource;
        this.message = message;
    }
}
