package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BasicBinaryContentServiceTest {

    @Autowired
    BasicBinaryContentService binaryContentService;

    @Autowired
    BinaryContentRepository binaryContentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("BinaryContent 생성 성공")
    void createBinaryContent_success() {
        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "bytes-data.png", "image/png",
                        "bytes-data".getBytes())
        );

        UUID binaryContentId = binaryContentService.createBinaryContent(request);
        flushAndClear();

        Optional<BinaryContent> saved = binaryContentRepository.findById(binaryContentId);
        assertThat(saved).isPresent();
    }

    @Test
    @DisplayName("BinaryContent 단건 조회 성공")
    void findBinaryContent_success() {
        byte[] image = "hello".getBytes();

        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "hello.png", "image/png", image)
        );

        UUID id = binaryContentService.createBinaryContent(request);
        flushAndClear();

        BinaryContentDto response = binaryContentService.findBinaryContent(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.fileName()).isEqualTo("hello.png");
        assertThat(response.size()).isEqualTo((long) image.length);
        assertThat(response.contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("존재하지 않는 BinaryContent 조회시 예외 발생")
    void findBinaryContent_fail_notFound() {
        assertThatThrownBy(() -> binaryContentService.findBinaryContent(UUID.randomUUID()))
                .isInstanceOf(DiscodeitException.class);
    }

    @Test
    @DisplayName("여러 BinaryContent 조회 성공")
    void findAllByIdIn_success() {
        UUID id1 = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "a.png", "image/png", "a".getBytes())
                )
        );

        UUID id2 = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "b.png", "image/png", "b".getBytes())
                )
        );

        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BinaryContentDto::id).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    @DisplayName("여러 BinaryContent 생성 성공")
    void createBinaryContents_success() {
        List<BinaryContent> result = binaryContentService.createBinaryContents(List.of(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "a.png", "image/png", "a".getBytes())
                ),
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "empty.png", "image/png", new byte[0])
                ),
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "b.png", "image/png", "b".getBytes())
                )
        ));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("BinaryContent 엔티티 조회 성공")
    void findBinaryContentEntity_success() {
        UUID id = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "entity.png", "image/png",
                                "entity".getBytes())
                )
        );

        BinaryContent entity = binaryContentService.findBinaryContentEntity(id);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFileName()).isEqualTo("entity.png");
    }

    @Test
    @DisplayName("BinaryContent 다운로드 성공")
    void downloadBinaryContent_success() throws Exception {
        byte[] bytes = "download".getBytes();
        UUID id = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "download.png", "image/png", bytes)
                )
        );

        ResponseEntity<?> response = binaryContentService.downloadBinaryContent(id);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("image/png");
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains(
                "download.png");

        Resource body = (Resource) response.getBody();
        assertThat(body).isNotNull();
        try (InputStream inputStream = body.getInputStream()) {
            assertThat(inputStream.readAllBytes()).isEqualTo(bytes);
        }
    }

    @Test
    @DisplayName("BinaryContent 삭제 성공")
    void deleteBinaryContent_success() {
        UUID id = binaryContentService.createBinaryContent(
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "delete.png", "image/png",
                                "delete".getBytes())
                )
        );

        binaryContentService.deleteBinaryContent(id);
        flushAndClear();

        assertThat(binaryContentRepository.findById(id)).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
