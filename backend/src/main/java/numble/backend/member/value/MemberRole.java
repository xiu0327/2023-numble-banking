package numble.backend.member.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numble.backend.common.exception.BusinessException;
import numble.backend.member.exception.MemberExceptionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ROLE_USER("사용자", "ROLE_USER"),
    ROLE_ADMIN("관리자", "ROLE_ADMIN")
    ;

    private String name;
    private String code;

    private static final Map<String, MemberRole> lookup = new HashMap();

    static{
        for (MemberRole role : MemberRole.values()) {
            lookup.put(role.code, role);
        }
    }

    public static MemberRole getLookUpByCode(String code){
        return lookup.get(code);
    }

    public static boolean containsKey(String code){
        return lookup.containsKey(code);
    }

    public static MemberRole findMemberRoleByName(String name){
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> memberRole.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new BusinessException(MemberExceptionType.NOT_FOUND_ROLE));
    }
}
