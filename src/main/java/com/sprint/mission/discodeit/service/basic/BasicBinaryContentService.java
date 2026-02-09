package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
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
    public UUID createBinaryContent(UUID ownerId, BinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                ownerId,
                request.type(),
                request.image()
        );

        binaryContentRepository.save(binaryContent);
        return binaryContent.getId();
    }

    @Override
    public BinaryContentResponse findBinaryContent(UUID binaryContentId) {
        BinaryContent binaryContent = getBinaryContentOrThrow(binaryContentId);

        return BinaryContentResponse.from(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIds(binaryContentIds);

        return binaryContents.stream().map(binaryContent ->
                BinaryContentResponse.from(binaryContent)
        ).toList();
    }

    @Override
    public void deleteBinaryContent(UUID binaryContentId) {
        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent 찾을 수 없습니다 binaryContentId: " + binaryContentId));
    }
}
