package numble.backend.member.api;

import lombok.RequiredArgsConstructor;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.member.application.MemberService;
import numble.backend.member.dto.request.AddFriendRequestDTO;
import numble.backend.member.dto.response.FriendshipDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /* 친구 추가 */
    @PostMapping("/api/friends")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponseDTO<Long> addFriend(@RequestBody AddFriendRequestDTO request){
        return memberService.addFriend(request.getUserId(), request.getFriendId());
    }

    /* 친구 목록 조회 */
    @GetMapping("/api/friends")
    public FriendshipDTO plainFriends(@RequestParam("userId") String userId){
        // 추후 JWT 로그인 기능 구현 후 시큐리티 컨텍스트에서 꺼내올 예정
        return new FriendshipDTO(userId, memberService.findFriend(userId));
    }
}
