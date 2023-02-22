package numble.backend.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.application.MemberService;
import numble.backend.member.dto.request.AddFriendRequestDTO;
import numble.backend.member.entity.Member;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    @Autowired MemberAuthService memberAuthService;
    @Autowired MemberService memberService;
    @Autowired EntityManager em;

    String password = "password123";
    String username = "사용자";

    @Before
    public void setting(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Transactional
    private Member createMember(String userId) {
        Member member = Member.builder()
                .userId(userId)
                .password(password)
                .username(username).build();
        Long memberId = memberAuthService.join(member);
        return em.find(Member.class, memberId);
    }

    @Test
    void 친구_추가() throws Exception {
        Member member = createMember("aaa123");
        Member friend = createMember("bbb123");

        AddFriendRequestDTO request = AddFriendRequestDTO.builder()
                .userId(member.getUserId())
                .friendId(friend.getUserId()).build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.resource").isNumber()
        ).andDo(print());
    }

    @Test
    void 친구_목록_조회() throws Exception {
        Member member = createMember("aaa123");
        Member friend = createMember("bbb123");

        memberService.addFriend(member.getUserId(), friend.getUserId());

        mockMvc.perform(get("/api/friends?userId=" + member.getUserId())
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.friends").isArray()
        ).andDo(print());
    }
}