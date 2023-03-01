package numble.backend.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import numble.backend.common.util.TestData;
import numble.backend.jwt.dto.GetTokenRequestDTO;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.member.application.MemberAuthService;
import numble.backend.member.dto.request.JoinMemberRequestDTO;
import numble.backend.member.dto.request.LoginMemberRequestDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberAuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    @Autowired MemberAuthService memberAuthService;
    @Autowired TestData testData;

    @Before
    public void setting(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void 회원_가입() throws Exception {
        JoinMemberRequestDTO request = JoinMemberRequestDTO.builder()
                .userId("userid111")
                .password(TestData.PASSWORD)
                .username("김땡땡").build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/auth/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.resource").isNumber()
        ).andDo(print());
    }

    @Test
    public void 로그인() throws Exception {
        String userId = testData.createMember("userid111");

        LoginMemberRequestDTO request = LoginMemberRequestDTO.builder()
                .userId(userId)
                .password(TestData.PASSWORD).build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.accessToken").isString()
        ).andDo(print());
    }

    @Test
    public void 로그인_실패() throws Exception {
        String userId = testData.createMember("userid111");

        LoginMemberRequestDTO request = LoginMemberRequestDTO.builder()
                .userId(userId)
                .password("1111").build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is4xxClientError()
        ).andExpect(jsonPath("$.code").isString()
        ).andDo(print());
    }

    @Test
    public void 토큰_재발행() throws Exception {
        String userId = testData.createMember("userid111");
        TokenDTO login = memberAuthService.login(userId, TestData.PASSWORD);

        GetTokenRequestDTO request = GetTokenRequestDTO.builder()
                .accessToken(login.getAccessToken())
                .refreshToken(login.getRefreshToken()).build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andDo(print());
    }
}
