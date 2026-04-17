package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentMapper binaryContentMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  private UUID binaryContentId;
  private String fileName;
  private String contentType;
  private byte[] bytes;
  private BinaryContent binaryContent;
  private BinaryContentDto binaryContentDto;

  @BeforeEach
  void setUp() {
    binaryContentId = UUID.randomUUID();
    fileName = "test.jpg";
    contentType = "image/jpeg";
    bytes = "test data".getBytes();

    binaryContent = BinaryContent.create(fileName, contentType, (long) bytes.length);
    ReflectionTestUtils.setField(binaryContent, "id", binaryContentId);

    binaryContentDto = new BinaryContentDto(
        binaryContentId,
        fileName,
        (long) bytes.length,
        contentType
    );
  }

  @Test
  @DisplayName("성공: 바이너리 콘텐츠 생성 성공")
  void createBinaryContentSuccess() {
    //given
    BinaryContentCreateDto request = new BinaryContentCreateDto(fileName, contentType,
        bytes, (long) bytes.length);

    given(binaryContentMapper.toEntity(request)).willReturn(binaryContent);
    given(binaryContentRepository.save(binaryContent)).willReturn(binaryContent);

    //when
    BinaryContent result = binaryContentService.create(request);

    //then
    assertEquals(binaryContent, result);
    then(binaryContentStorage).should().put(binaryContentId, bytes);
  }

  @Test
  @DisplayName("성공: 바이너리 콘텐츠 조회 성공")
  void findBinaryContentSuccess() {
    //given
    given(binaryContentRepository.findById(eq(binaryContentId))).willReturn(
        Optional.of(binaryContent));
    given(binaryContentMapper.toDto(eq(binaryContent))).willReturn(binaryContentDto);

    //when
    BinaryContentDto result = binaryContentService.find(binaryContentId);

    //then
    assertEquals(binaryContentDto, result);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 바이너리 콘텐츠 조회 시 예외 발생")
  void findBinaryContentWithNotExistedIdFailure() {
    //given
    given(binaryContentRepository.findById(eq(binaryContentId))).willReturn(Optional.empty());

    //when & then
    assertThrows(BinaryContentNotFoundException.class,
        () -> binaryContentService.find(binaryContentId));
  }

  @Test
  @DisplayName("성공: 아이디 목록으로 바이너리 콘텐츠 목록 조회 성공")
  void findAllByIdInSuccess() {
    //given
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<UUID> ids = Arrays.asList(id1, id2);

    BinaryContent content1 = BinaryContent.create("file1.jpg", "image/jpeg", 100L);
    ReflectionTestUtils.setField(content1, "id", id1);

    BinaryContent content2 = BinaryContent.create("file2.jpg", "image/png", 200L);
    ReflectionTestUtils.setField(content2, "id", id2);

    List<BinaryContent> contents = Arrays.asList(content1, content2);

    BinaryContentDto dto1 = new BinaryContentDto(id1, "file1.jpg", 100L, "image/jpeg");
    BinaryContentDto dto2 = new BinaryContentDto(id2, "file2.jpg", 200L, "image/png");

    given(binaryContentRepository.findAllById(eq(ids))).willReturn(contents);
    given(binaryContentMapper.toDto(eq(content1))).willReturn(dto1);
    given(binaryContentMapper.toDto(eq(content2))).willReturn(dto2);

    //when
    List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

    //then
    assertThat(result).containsExactly(dto1, dto2);
  }

  @Test
  @DisplayName("성공: 바이너리 콘텐츠 삭제 성공")
  void deleteBinaryContentSuccess() {
    //given
    given(binaryContentRepository.existsById(binaryContentId)).willReturn(true);

    //when
    binaryContentService.delete(binaryContentId);

    //then
    then(binaryContentRepository).should().deleteById(binaryContentId);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 바이너리 콘텐츠 삭제 실패")
  void deleteBinaryContentWithNotExistedIdFailure() {
    //given
    given(binaryContentRepository.existsById(eq(binaryContentId))).willReturn(false);

    //when & then
    assertThrows(BinaryContentNotFoundException.class,
        () -> binaryContentService.delete(binaryContentId));
  }
}