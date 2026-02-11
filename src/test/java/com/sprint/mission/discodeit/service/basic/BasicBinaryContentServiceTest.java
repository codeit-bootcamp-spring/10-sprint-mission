package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.response.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BasicBinaryContentServiceTest {


    @Autowired
    BasicBinaryContentService binaryContentService;

    @Autowired
    BinaryContentRepository binaryContentRepository;

    UUID ownerId;


    @BeforeEach
    void setUp() {
        FileIOHelper.flushData();
        ownerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("BinaryContent 생성 성공")
    void createBinaryContent_success() {
        // given
        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile(
                        "file",
                        "bytes-data.png",
                        "image/png",
                        "bytes-data".getBytes()
                )
        );

        // when
        UUID binaryContentId =
                binaryContentService.createBinaryContent(ownerId, request);

        // then
        Optional<BinaryContent> saved =
                binaryContentRepository.findById(binaryContentId);

        assertThat(saved).isPresent();
        assertThat(saved.get().getOwnerId()).isEqualTo(ownerId);
        assertThat(saved.get().getBinaryContentOwnerType()).isEqualTo(BinaryContentOwnerType.USER);
    }

    @Test
    @DisplayName("BinaryContent 단건 조회 성공")
    void findBinaryContent_success() {
        // given
        byte[] image = "hello".getBytes();

        BinaryContentRequest request = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile(
                        "file",
                        "hello.png",
                        "image/png",
                        image
                )
        );

        UUID id = binaryContentService.createBinaryContent(ownerId, request);

        // when
        BinaryContentResponse response =
                binaryContentService.findBinaryContent(id);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.bytes()).isEqualTo(image);
        assertThat(binaryContentRepository.findById(id).orElseThrow().getOwnerId())
                .isEqualTo(ownerId);
    }

    @Test
    @DisplayName("존재하지 않는 BinaryContent 조회시 예외 발생")
    void findBinaryContent_fail_notFound() {
        // given
        UUID wrongId = UUID.randomUUID();

        // expect
        assertThatThrownBy(() -> binaryContentService.findBinaryContent(wrongId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("여러 BinaryContent 조회 성공")
    void findAllByIdIn_success() {
        // given
        UUID id1 = binaryContentService.createBinaryContent(
                ownerId,
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "a.png",
                                "image/png",
                                "a".getBytes()
                        )
                )
        );

        UUID id2 = binaryContentService.createBinaryContent(
                ownerId,
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "b.png",
                                "image/png",
                                "b".getBytes()
                        )
                )
        );

        List<UUID> ids = List.of(id1, id2);

        // when
        List<BinaryContentResponse> result =
                binaryContentService.findAllByIdIn(ids);

        // then
        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(BinaryContentResponse::id)
                .containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    @DisplayName("BinaryContent 삭제 성공")
    void deleteBinaryContent_success() {
        // given
        UUID id = binaryContentService.createBinaryContent(
                ownerId,
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "delete.png",
                                "image/png",
                                "delete".getBytes()
                        )
                )
        );

        // when
        binaryContentService.deleteBinaryContent(id);

        // then
        Optional<BinaryContent> result =
                binaryContentRepository.findById(id);

        assertThat(result).isEmpty();
    }

}
