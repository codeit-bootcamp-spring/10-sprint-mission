package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.exception.user.UserNameDuplicateException;
import com.sprint.mission.discodeit.exceptionhandler.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om; // 객체 <-> json 직렬화를 위한 객체 매퍼

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatusService userStatusService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    public class post_test {

        @Test
        void user_post_success() throws Exception {
            // given
            UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abc@a.com", "abc");
            UUID id = UUID.randomUUID();

            given(userService.create(eq(req), isNull())).willReturn(
                new UserDto(id, req.username(), req.email(), null, false));

            // 컨트롤러의 Post 메서드는 consumes=MediaType.MULTIPART_FORM_DATA_VALUE
            // MultiPart의 한 부분을 만듬
            MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest", // 컨트롤러에 명시된 이름과 동일해야 한다.
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE, // 해당 파트의 타입은 JSON
                om.writeValueAsBytes(req) // req 객체를 바이트로 변환하여 파트의 body로 넣음
            );

            // when
            ResultActions actions = mockMvc.perform(
                multipart( // 컨트롤러의 post 메서드가 consumes = MULTIPART_FORM_DATA이므로 이 방식을 써야 함.
                    "/api/users")
                    .file(userCreateRequestPart)
                    .accept(MediaType.APPLICATION_JSON) // JSON 형태의 응답
            );

            // then
            actions.andExpect(status().isCreated()) // 성공 시 예상되는 STATUS는 isCreated(201)
                .andExpect(
                    jsonPath("$.id").value(id.toString())) // 성공 시 예상되는 id값은 일전에 설정해두었던 id 값과 동일
                .andExpect(jsonPath("$.username").value("abc"))
                .andExpect(jsonPath("$.email").value("abc@a.com"))
                .andExpect(jsonPath("$.profile").value(nullValue()))
                .andExpect(jsonPath("$.online").value(false));
        }

        @Test
        @DisplayName("유효하지 않은 이메일로 시도 시 실패")
        void user_post_fail_with_invalid_email() throws Exception {
            // given

            UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abcdef",
                "abc"); // 이메일의 형태가 아님
            // 컨트롤러가 실행되기 전 Spring MVC 가 컨트롤러 메서드 진입 전에 자동 검증하여 예외를 발생시킴

            MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                om.writeValueAsBytes(req)
            );

            // when
            ResultActions actions = mockMvc.perform(
                multipart("/api/users")
                    .file(userCreateRequestPart)
                    .accept(MediaType.APPLICATION_JSON)
            );

            // then
            actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("FIELD_NOT_VALID"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.email").exists());
        }

        @Test
        @DisplayName("중복된 username으로 시도 시 실패")
        void user_post_fail_with_duplicate_username() throws Exception {
            // given
            UserCreateRequestDTO req = new UserCreateRequestDTO("abc", "abc@a.com", "abc");
            MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                om.writeValueAsBytes(req)
            );

            // 이름이 중복 -> UserNameDuplicationException 발행함.
            given(userService.create(eq(req), isNull()))
                .willThrow(new UserNameDuplicateException(req.username()));

            // when
            ResultActions actions = mockMvc.perform(
                multipart("/api/users")
                    .file(userCreateRequestPart)
                    .accept(MediaType.APPLICATION_JSON)
            );

            // then
            actions.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_NAME_DUPLICATE"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.details.username").value("abc"));
        }
    }


}
