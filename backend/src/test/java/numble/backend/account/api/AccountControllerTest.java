package numble.backend.account.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import numble.backend.account.application.AccountService;
import numble.backend.account.dto.CreateAccountRequestDTO;
import numble.backend.account.dto.FindAccountRequestDTO;
import numble.backend.account.dto.TransferRequestDTO;
import numble.backend.common.util.TestData;
import numble.backend.friendship.application.FriendshipService;
import numble.backend.jwt.dto.TokenDTO;
import numble.backend.member.application.MemberAuthService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired FriendshipService friendshipService;
    @Autowired AccountService accountService;
    @Autowired MemberAuthService memberAuthService;
    @Autowired TestData testData;

    @Before
    public void setting(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void 계좌_생성() throws Exception {
        String userId = testData.createMember("userid111");
        TokenDTO login = memberAuthService.login(userId, TestData.PASSWORD);
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .userId(userId)
                .password("1234").build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(
                post("/api/accounts")
                        .header(HttpHeaders.AUTHORIZATION, login.getGrantType() + " " + login.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.resource").isString()
        ).andDo(print());
    }

    @Test
    void 계좌_조회() throws Exception {
        String userId = testData.createMember("userid111");
        TokenDTO login = memberAuthService.login(userId, TestData.PASSWORD);
        String accountNumber = accountService.create(userId, TestData.ACCOUNT_PASSWORD).getResource();
        testData.settingMoney(accountNumber);

        FindAccountRequestDTO request = FindAccountRequestDTO.builder()
                .accountNumber(accountNumber)
                .accountPassword(TestData.ACCOUNT_PASSWORD).build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(get("/api/accounts")
                .header(HttpHeaders.AUTHORIZATION, login.getGrantType() + " " + login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.money").isNumber()
        ).andDo(print());
    }

    @Test
    void 정상_계좌_이체() throws Exception {
        String fromUserId = testData.createMember("userid111");
        TokenDTO login = memberAuthService.login(fromUserId, TestData.PASSWORD);
        String toUserId = testData.createMember("userid222");
        friendshipService.addFriend(fromUserId, toUserId);
        String from = accountService.create(fromUserId, TestData.ACCOUNT_PASSWORD).getResource();
        String to = accountService.create(toUserId, TestData.ACCOUNT_PASSWORD).getResource();
        testData.settingMoney(from);

        int money = 10000;

        TransferRequestDTO request = TransferRequestDTO.builder()
                .fromAccountNumber(from)
                .toAccountNumber(to)
                .inputPassword(TestData.ACCOUNT_PASSWORD)
                .money(money).build();

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/accounts/transfer")
                .header(HttpHeaders.AUTHORIZATION, login.getGrantType() + " " + login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andExpect(status().is2xxSuccessful()
        ).andDo(print());

        int result = testData.findAccountByNumber(to).getAmount();
        assertThat(result).isEqualTo(money);
    }
}