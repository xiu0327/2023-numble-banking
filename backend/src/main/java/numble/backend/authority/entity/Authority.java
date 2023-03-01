package numble.backend.authority.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.backend.member.value.MemberRole;

import javax.persistence.*;

@Entity
@Table(name = "authority")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    @Enumerated(EnumType.STRING)
    private MemberRole authorityName;

    public String getAuthorityName() {
        return this.authorityName.toString();
    }
}

