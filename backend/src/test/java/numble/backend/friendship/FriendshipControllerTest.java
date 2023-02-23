package numble.backend.friendship;

import com.fasterxml.jackson.databind.ObjectMapper;
import numble.backend.common.util.TestData;
import numble.backend.friendship.application.FriendshipService;
import numble.backend.friendship.dto.AddFriendRequestDTO;
import org.junit.Before;
import org.junit.jupiter.api.Test;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class FriendshipControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    @Autowired FriendshipService friendshipService;
    @Autowired TestData testData;

    @Before
    public void setting(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void 친구_추가() throws Exception {
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");

        AddFriendRequestDTO request = AddFriendRequestDTO.builder()
                .ownerId(ownerId)
                .friendId(friendId).build();

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
        String ownerId = testData.createMember("owner111");
        String friendId = testData.createMember("friend111");

        friendshipService.addFriend(ownerId, friendId);

        mockMvc.perform(get("/api/friends?ownerId=" + ownerId + "&page=0")
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(jsonPath("$.friends").isArray()
        ).andDo(print());
    }
}