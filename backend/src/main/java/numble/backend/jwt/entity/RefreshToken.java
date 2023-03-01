package numble.backend.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "token_key")
    private String key;

    @Column(name = "token_value", nullable = false)
    private String value;

    public void updateValue(String token){
        this.value = token;
    }

}