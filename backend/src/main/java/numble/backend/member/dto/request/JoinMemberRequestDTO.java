package numble.backend.member.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import numble.backend.member.entity.Member;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinMemberRequestDTO {

    private String userId;
    private String password;
    private String username;


    public Member toEntity(){
        return Member.builder()
                .userId(userId)
                .password(password)
                .username(username).build();
    }
}
