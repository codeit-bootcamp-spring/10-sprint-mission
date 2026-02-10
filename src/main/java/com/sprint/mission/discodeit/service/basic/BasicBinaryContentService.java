package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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
                        request.bytes(),
                        request.profileUserId(),
                        request.messageId()
                )
        );

        return saved.getId();
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        requireNonNull(id, "id");

        BinaryContent found = findEntityOrThrow(id);
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

        findEntityOrThrow(id);
        binaryContentRepository.delete(id);
    }

    @Override
    public BinaryContent findEntity(UUID id) {
        requireNonNull(id, "id");
        return findEntityOrThrow(id);
    }

    @Override
    public List<BinaryContent> findAllEntitiesByIdIn(List<UUID> ids) {
        requireNonNull(ids, "ids");
        return binaryContentRepository.findAllByIdIn(ids);
    }

    private BinaryContent findEntityOrThrow(UUID id) {
        BinaryContent found = binaryContentRepository.findById(id);
        if (found == null) {
            throw new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
        }
        return found;
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
