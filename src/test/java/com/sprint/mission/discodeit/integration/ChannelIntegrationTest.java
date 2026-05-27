package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChannelIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ChannelRepository channelRepository;

    @AfterEach
    void tearDown() {
        channelRepository.deleteAll();
    }

    @Test
    @DisplayName("채널 생성 통합 테스트 - DB에 실제 저장")
    void 채널_생성_통합() {
        // given
        String json = """
                  {"name": "통합 테스트 채널", "description": "통합 테스트 채널입니다."}
                  """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // when
        ResponseEntity<ChannelDTO> response = testRestTemplate.postForEntity(
                "/api/channels/public",
                request,
                ChannelDTO.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("통합 테스트 채널");

        assertThat(channelRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("채널 단건 조회 - 통합")
    void 채널_단건_조회_성공() {
        // given
        String json = """
                  {"name": "통합 테스트 채널", "description": "통합 테스트 채널입니다."}
                  """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<ChannelDTO> response = testRestTemplate.postForEntity(
                "/api/channels/public",
                request,
                ChannelDTO.class
        );
        UUID channelId = response.getBody().id();

        // when
        ResponseEntity<ChannelDTO> getResponse = testRestTemplate.getForEntity(
                "/api/channels/{channelId}",
                ChannelDTO.class,
                channelId
        );

        // then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo("통합 테스트 채널");

        assertThat(channelRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("채널 수정 통합 테스트")
    void 채널_수정_통합_테스트_성공(){
        // given
        String json = """
                  {"name": "통합 테스트 채널", "description": "통합 테스트 채널입니다."}
                  """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<ChannelDTO> postResponse = testRestTemplate.postForEntity(
                "/api/channels/public",
                request,
                ChannelDTO.class
        );
        UUID channelId = postResponse.getBody().id();
        // --- 채널 생성 ----
        String updateJson = """
                  {"newName": "수정된 통합 테스트 채널", "newDescription": "수정된 통합 테스트 채널입니다."}
                  """;

        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson, headers);

        // when
        ResponseEntity<ChannelDTO> updateResponse = testRestTemplate.exchange(
                "/api/channels/{channelId}",
                HttpMethod.PATCH,
                updateRequest,
                ChannelDTO.class,
                channelId
        );

        // then
        assertThat(updateResponse.getBody().name()).isEqualTo("수정된 통합 테스트 채널");
        assertThat(channelRepository.count()).isEqualTo(1);
    }


}
