package com.sprint.mission.discodeit.binarycontent.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.event.FileUploadEvent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentException;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.presentation.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.support.mapper.MapperContainer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

  @Mock
  private BinaryContentRepository repository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Spy
  private final BinaryContentMapper mapper = MapperContainer.get(BinaryContentMapper.class);

  @InjectMocks
  private BinaryContentService binaryContentService;

  @Test
  @DisplayName("create - 파일을 정상 업로드하고 DTO를 반환한다.")
  void create_success() {
    FileUploadRequest request = new FileUploadRequest("test.txt", "text/plain", 11L,
        "Hello World".getBytes());

    ArgumentCaptor<List<BinaryContent>> captor = ArgumentCaptor.forClass(List.class);
    given(repository.saveAll(captor.capture())).willReturn(List.of());

    List<BinaryContentDto> result = binaryContentService.create(List.of(request));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).fileName()).isEqualTo("test.txt");
    assertThat(result.get(0).size()).isEqualTo(11L);

    verify(repository).saveAll(anyList());
    verify(eventPublisher).publishEvent(any(FileUploadEvent.class));

    List<BinaryContent> savedEntities = captor.getValue();
    assertThat(savedEntities).hasSize(1);
    assertThat(savedEntities.get(0).getFileName()).isEqualTo("test.txt");
  }

  @Test
  @DisplayName("find - 단일 BinaryContent를 정상 조회한다.")
  void find_success() {
    UUID id = UUID.randomUUID();
    BinaryContent entity = BinaryContent.builder()
        .fileName("test.txt")
        .contentType("text/plain")
        .size(100L)
        .build();

    given(repository.findById(id)).willReturn(Optional.of(entity));

    BinaryContentDto result = binaryContentService.find(id);

    assertThat(result.fileName()).isEqualTo("test.txt");
    assertThat(result.size()).isEqualTo(100L);
  }

  @Test
  @DisplayName("find - ID가 존재하지 않으면 예외를 던진다.")
  void find_fail_not_found() {
    UUID id = UUID.randomUUID();

    given(repository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> binaryContentService.find(id))
        .isInstanceOf(BinaryContentException.class)
        .hasFieldOrPropertyWithValue("errorCode", BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND);
  }

  @Test
  @DisplayName("findAllByIdIn - 여러 ID로 목록을 정상 조회한다.")
  void findAllByIdIn_success() {
    UUID id = UUID.randomUUID();
    BinaryContent entity = BinaryContent.builder()
        .fileName("test.txt")
        .contentType("text/plain")
        .size(100L)
        .build();

    given(repository.findAllById(List.of(id))).willReturn(List.of(entity));

    List<BinaryContentDto> result = binaryContentService.findAllByIdIn(List.of(id));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).fileName()).isEqualTo("test.txt");
  }

  @Test
  @DisplayName("existsOrThrow - 존재하는 ID이면 예외를 던지지 않는다.")
  void existsOrThrow_success() {
    UUID id = UUID.randomUUID();
    given(repository.existsSuccessById(id)).willReturn(true);

    binaryContentService.existsOrThrow(id);
  }

  @Test
  @DisplayName("existsOrThrow - 존재하지 않는 ID이면 예외를 던진다.")
  void existsOrThrow_fail() {
    UUID id = UUID.randomUUID();
    given(repository.existsSuccessById(id)).willReturn(false);

    assertThatThrownBy(() -> binaryContentService.existsOrThrow(id))
        .isInstanceOf(BinaryContentException.class)
        .hasFieldOrPropertyWithValue("errorCode", BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND);
  }
}
