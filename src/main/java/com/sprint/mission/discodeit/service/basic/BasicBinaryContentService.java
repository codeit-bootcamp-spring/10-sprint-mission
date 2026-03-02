package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.utils.ImageBinaryConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID createBinaryContent(UUID ownerId, BinaryContentRequest request) {
        if (request == null || request.file() == null || request.file().isEmpty()) {
            return null;
        }

        BinaryContent binaryContent = new BinaryContent(
                ownerId,
                request.type(),
                ImageBinaryConverter.convert(request.file()),
                request.file().getContentType(),
                request.file().getOriginalFilename()
        );

        binaryContentRepository.save(binaryContent);
        return binaryContent.getId();
    }

    @Override
    public BinaryContent findBinaryContent(UUID binaryContentId) {
        return getBinaryContentOrThrow(binaryContentId);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIds(binaryContentIds);
    }

    @Override
    public void deleteBinaryContent(UUID binaryContentId) {
        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new ApiException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
                        "BinaryContent 찾을 수 없습니다 binaryContentId: " + binaryContentId));
    }
}
