package numble.backend.member.api;

import lombok.RequiredArgsConstructor;
import numble.backend.jwt.dto.GetTokenRequestDTO;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.dto.request.JoinMemberRequestDTO;
import numble.backend.member.dto.request.LoginMemberRequestDTO;
import numble.backend.member.dto.response.MemberBasicResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/auth/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberBasicResponseDTO joinMember(@RequestBody JoinMemberRequestDTO request){
        Long resource = memberAuthService.join(request.toEntity());
        return new MemberBasicResponseDTO(resource, "회원가입이 완료되었습니다.");

    }

    @PostMapping("/auth/login")
    public TokenDTO login(@RequestBody LoginMemberRequestDTO request){
        return memberAuthService.login(request.getUserId(), request.getPassword());
    }

    @PostMapping("/auth/reissue")
    public TokenDTO reissue(@RequestBody GetTokenRequestDTO request){
        return memberAuthService.reissue(request.getAccessToken(), request.getRefreshToken());
    }
}
