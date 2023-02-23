package numble.backend.friendship.api;

import lombok.RequiredArgsConstructor;
import numble.backend.common.dto.BasicResponseDTO;
import numble.backend.friendship.application.FriendshipService;
import numble.backend.friendship.dto.AddFriendRequestDTO;
import numble.backend.friendship.dto.FriendshipDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    /* 친구 추가 */
    @PostMapping("/api/friends")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponseDTO<Long> addFriend(@RequestBody AddFriendRequestDTO request){
        return friendshipService.addFriend(request.getOwnerId(), request.getFriendId());
    }

    /* 친구 목록 조회 */
    @GetMapping("/api/friends")
    public FriendshipDTO plainFriends(
            @RequestParam("ownerId") String ownerId,
            @RequestParam("page") int page,
            @RequestParam(value = "size", defaultValue = "20") int size){
        // 추후 JWT 로그인 기능 구현 후 시큐리티 컨텍스트에서 꺼내올 예정
        return new FriendshipDTO(ownerId, friendshipService.findFriend(ownerId, PageRequest.of(page, size)));
    }
}
