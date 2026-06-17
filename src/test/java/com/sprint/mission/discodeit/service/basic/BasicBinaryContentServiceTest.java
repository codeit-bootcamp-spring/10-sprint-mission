package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentMapper binaryContentMapper;
    @Mock private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicBinaryContentService basicBinaryContentService;

    @Test
    @DisplayName("유효한 데이터로 BinaryContent를 생성한다.")
    void create_success() {
        // given
        byte[] bytes = "file content".getBytes();
        CreateBinaryContentRequestDto request =
            new CreateBinaryContentRequestDto("test.png", "image/png", bytes.length, bytes);

        BinaryContent entity = new BinaryContent("test.png", "image/png", (long) bytes.length);
        UUID savedId = UUID.randomUUID();
        ReflectionTestUtils.setField(entity, "id", savedId);

        BinaryContentDto expectedDto =
            new BinaryContentDto(savedId, "test.png", "image/png", (long) bytes.length, BinaryContentStatus.SUCCESS, null);

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(entity);
        given(binaryContentMapper.toDto(entity)).willReturn(expectedDto);

        // when
        BinaryContentDto result = basicBinaryContentService.create(request);

        // then
        assertEquals(expectedDto, result);
        verify(binaryContentStorage).put(savedId, bytes);
    }

    @Test
    @DisplayName("존재하는 ID로 BinaryContent를 조회한다.")
    void find_success() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContent entity = new BinaryContent("img.png", "image/png", 1024L);
        BinaryContentDto expectedDto =
            new BinaryContentDto(id, "img.png", "image/png", 1024L, BinaryContentStatus.SUCCESS, null);

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(entity));
        given(binaryContentMapper.toDto(entity)).willReturn(expectedDto);

        // when
        BinaryContentDto result = basicBinaryContentService.find(id);

        // then
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 NoSuchElementException이 발생한다.")
    void find_fail_not_found() {
        // given
        UUID fakeId = UUID.randomUUID();
        given(binaryContentRepository.findById(fakeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(java.util.NoSuchElementException.class,
            () -> basicBinaryContentService.find(fakeId));
    }

    @Test
    @DisplayName("여러 ID로 BinaryContent 목록을 조회한다.")
    void findAllByIdIn_success() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);

        BinaryContent e1 = new BinaryContent("a.png", "image/png", 100L);
        BinaryContent e2 = new BinaryContent("b.pdf", "application/pdf", 200L);

        given(binaryContentRepository.findAllById(ids)).willReturn(List.of(e1, e2));
        given(binaryContentMapper.toDto(e1))
            .willReturn(new BinaryContentDto(id1, "a.png", "image/png", 100L, BinaryContentStatus.SUCCESS, null));
        given(binaryContentMapper.toDto(e2))
            .willReturn(new BinaryContentDto(id2, "b.pdf", "application/pdf", 200L, BinaryContentStatus.SUCCESS, null));

        // when
        List<BinaryContentDto> results = basicBinaryContentService.findAllByIdIn(ids);

        // then
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("존재하는 BinaryContent를 삭제한다.")
    void delete_success() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContent entity = new BinaryContent("del.png", "image/png", 512L);

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(entity));

        // when
        basicBinaryContentService.delete(id);

        // then
        verify(binaryContentRepository).delete(entity);
    }
}