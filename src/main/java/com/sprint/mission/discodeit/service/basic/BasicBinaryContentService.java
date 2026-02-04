package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID create(BinaryContentCreateRequest request) {
        requireNonNull(request, "request");

        BinaryContent saved = binaryContentRepository.save(
                new BinaryContent(
                        request.fileName(),
                        request.contentType(),
                        request.data(),
                        request.profileUserId(),
                        request.messageId()
                )
        );

        return saved.getId();
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        requireNonNull(id, "id");

        BinaryContent found = binaryContentRepository.findById(id);
        if (found == null) {
            throw new StatusNotFoundException();
        }

        return toResponse(found);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        requireNonNull(ids, "ids");

        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");

        // File 구현체가 "없어도 조용히 삭제"라서 서비스에서 예외 보장
        if (binaryContentRepository.findById(id) == null) {
            throw new StatusNotFoundException();
        }

        binaryContentRepository.delete(id);
    }

    private BinaryContentResponse toResponse(BinaryContent bc) {
        return new BinaryContentResponse(
                bc.getId(),
                bc.getFileName(),
                bc.getContentType(),
                bc.getProfileUserId(),
                bc.getMessageId(),
                bc.getCreatedAt()
        );
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
