/*
package com.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.repo.domain.user.dto.UserRequestDto;
import com.repo.domain.user.entity.User;
import com.repo.domain.user.repository.UserRepository;
import com.repo.security.jwt.JwtService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)  // 테스트 메서드 순서 지정
class JwtServiceScenarioTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("태스터3")
                .password("password")
                .authorities(User.Authorities.ADMIN)
                .status(User.Status.NORMAL)
                .build();
    }

    */
/*@Test
    @Order(1)
    @DisplayName("회원 가입")
    void testUserSignupScenario() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto(user.getUsername(), "password", "nickname", null);
        String signupRequestJson = objectMapper.writeValueAsString(userRequestDto);

        var signupResponse = mockMvc.perform(post("/api/signup")
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(signupRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Signup Response: " + signupResponse.getResponse().getContentAsString());
    }*//*


    @Test
    @Order(2)
    @DisplayName("로그인 및 토큰 발급")
    void testTokenGenerationScenario() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto(user.getUsername(), "password", "nickname", null);
        String loginRequestJson = objectMapper.writeValueAsString(userRequestDto);

        var loginResponse = mockMvc.perform(post("/api/sign")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        accessToken = JsonPath.read(loginResponse.getResponse().getContentAsString(), "$.accessToken");
        refreshToken = JsonPath.read(loginResponse.getResponse().getContentAsString(), "$.refreshToken");
    }

    @Test
    @Order(3)
    @DisplayName("인증이 필요한 엔드포인트")
    void testProtectedEndpointAccessScenario() throws Exception {
        mockMvc.perform(get("/api/profile")
                                .header("Authorization",accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("리프레시 토큰으로 AccessToken 발급")
    void testRefreshTokenScenario() throws Exception {
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        String refreshRequestJson = objectMapper.writeValueAsString(refreshRequest);

        var refreshResponse = mockMvc.perform(post("/api/refresh")
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(refreshRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        accessToken = JsonPath.read(refreshResponse.getResponse().getContentAsString(), "$.accessToken");
    }

    @Test
    @Order(5)
    @DisplayName("새로운 AccessToken으로 인증이 필요한 엔드포인트 테스트")
    void testNewAccessTokenAccessScenario() throws Exception {
        mockMvc.perform(get("/api/profile")
                                .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("만료된 AccessToken 사용 시나리오 테스트")
    void testExpiredAccessTokenScenario() throws Exception {
        String expiredToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZDMiLCJpYXQiOjE3MjU1Mjg5NzIsImlkIjozLCJhdXRob3JpdGllcyI6IkFETUlOIiwic3RhdHVzIjoiTk9STUFMIiwiZXhwIjoxNzI1NTI4OTc4fQ.-hbmTDxC_IbWk88_twZ9eRQBBd4ulVCylAfsw_7sBD0";
        mockMvc.perform(get("/api/profile")
                                .header("Authorization", expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
*/
