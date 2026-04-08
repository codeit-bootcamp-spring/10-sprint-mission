//package com.sprint.mission.discodeit;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//class DiscodeitApplicationTests {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//
//    // Mock이 아니라 진짜 Repository를 주입받아 DB 상태를 교차 검증
//    @Autowired private UserRepository userRepository;
//    @Autowired private UserService userService;
//
//    @Autowired private EntityManager em;
//
//    @Test
//    @DisplayName("유저 생성 통합 테스트 - API 호출 후 실제 DB에 유저가 저장된다.")
//    void createUser_Integration_Success() throws Exception {
//        // given
//        CreateUserRequestDto request = new CreateUserRequestDto("integrationUser", "integ@test.com", "password123", null);
//
//        MockMultipartFile requestPart = new MockMultipartFile(
//            "userCreateRequest", "request.json", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request)
//        );
//
//        // when (HTTP API 호출)
//        mockMvc.perform(multipart("/api/users")
//                .file(requestPart)
//                .accept(MediaType.APPLICATION_JSON))
//            // then 1단계: HTTP 응답 검증
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.username").value("integrationUser"));
//
//        // 💡 then 2단계: 실제 DB 검증
//        User savedUser = userRepository.findByUsername("integrationUser").orElseThrow();
//        assertThat(savedUser.getEmail()).isEqualTo("integ@test.com");
//    }
//
//    @Test
//    @DisplayName("유저 목록 조회 통합 테스트 - 미리 DB에 넣은 데이터가 API로 잘 조회된다.")
//    void findAllUsers_Integration_Success() throws Exception {
//        // given
//        CreateUserRequestDto request = new CreateUserRequestDto("dbUser", "db@test.com", "pass1234", null);
//        userService.create(request);
//
//        em.flush();
//        em.clear();
//
//        // when & then
//        mockMvc.perform(get("/api/users")
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$[0].username").value("dbUser")) // DB에 넣은 값이 그대로 나오는지 확인
//            .andExpect(jsonPath("$[0].email").value("db@test.com"));
//    }
//}