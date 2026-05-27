package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserIntegrationTest {
    /*
    Http Client, 실제 네트워크 스택을 거침
    multipart/form-data, 파일 업로드 지원
     */
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    /*
    각 테스트가 끝난 후 DB 초기화
     */
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 생성 통합 테스트 - DB에 실제 저장")
    void 유저_생성_통합() {
        // given
        // multipart/form-data 요청 구성
        // "userCreateRequest" 파트에 JSON을 넣고, Content-Type을 명시해야 한다.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.APPLICATION_JSON);

        // JSON 문자열을 직접 작성해서 파트로 추가
        String json = """
                  {"username": "승주", "email": "jsj@naver.com", "password": "12345678"}
                  """;
        HttpEntity<String> jsonPart = new HttpEntity<>(json, partHeaders);
        body.add("userCreateRequest", jsonPart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // when
        // 실제 HTTP POST 요청 전송 → Controller → Service → Repository → H2 DB
        ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(
                "/api/users",
                request,
                UserDTO.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("승주");

        // DB에 실제로 저장됐는지 확인
        assertThat(userRepository.count()).isEqualTo(1);
    }
}
