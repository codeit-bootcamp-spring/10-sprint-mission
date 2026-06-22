package com.sprint.mission.discodeit.binarycontent.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.binarycontent.application.service.BinaryContentService;
import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.provider.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.global.infra.storage.download.StorageDownloader;
import com.sprint.mission.discodeit.support.mapper.GlobalApiMapperTestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BinaryContentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalApiMapperTestConfig.class)
class BinaryContentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BinaryContentService binaryContentService;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  @MockitoBean
  private StorageDownloader downloader;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("create - 파일을 정상 업로드하고 BinaryContentDto 목록을 반환한다.")
  void create_success() throws Exception {
    MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain",
        "Hello World".getBytes());

    UUID id = UUID.randomUUID();
    BinaryContent entity = BinaryContent.builder()
        .fileName("test.txt")
        .size(11L)
        .contentType("text/plain")
        .build();
    org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", id);

    ArgumentCaptor<List<FileUploadRequest>> captor = ArgumentCaptor.forClass(List.class);
    given(binaryContentService.create(captor.capture())).willReturn(List.of(entity));

    mockMvc.perform(multipart("/api/binaryContents")
            .file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fileName").value("test.txt"))
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    List<FileUploadRequest> requests = captor.getValue();
    assertThat(requests).hasSize(1);
    assertThat(requests.get(0).fileName()).isEqualTo("test.txt");
    assertThat(requests.get(0).contentType()).isEqualTo("text/plain");
    assertThat(requests.get(0).size()).isEqualTo(11L);
  }

  @Test
  @DisplayName("find - ID로 파일 메타데이터를 정상 조회한다.")
  void find_success() throws Exception {
    UUID id = UUID.randomUUID();
    BinaryContent entity = BinaryContent.builder()
        .fileName("test.txt")
        .size(100L)
        .contentType("text/plain")
        .build();
    org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", id);

    given(binaryContentService.find(id)).willReturn(entity);

    mockMvc.perform(get("/api/binaryContents/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fileName").value("test.txt"));
  }

  @Test
  @DisplayName("findMany - 여러 ID로 메타데이터 목록을 정상 조회한다.")
  void findMany_success() throws Exception {
    UUID id = UUID.randomUUID();
    BinaryContent entity = BinaryContent.builder()
        .fileName("test.txt")
        .size(100L)
        .contentType("text/plain")
        .build();
    org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", id);

    given(binaryContentService.findAllByIdIn(List.of(id))).willReturn(List.of(entity));

    mockMvc.perform(get("/api/binaryContents")
            .param("ids", id.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fileName").value("test.txt"));
  }

  @Test
  @DisplayName("download - ID를 통해 파일을 정상 다운로드한다.")
  void download_success() throws Exception {
    UUID id = UUID.randomUUID();
    String mockUrl = "mock-url";
    ResponseEntity responseEntity = ResponseEntity.ok().body("Hello World".getBytes());

    given(binaryContentStorage.downloadUrl(id)).willReturn(mockUrl);
    given(downloader.download(mockUrl)).willReturn(responseEntity);

    mockMvc.perform(get("/api/binaryContents/{id}/download", id))
        .andExpect(status().isOk());
  }
}
