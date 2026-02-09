package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.bytes(), request.contentType());
        BinaryContent newBinaryContent = binaryContentRepository.save(binaryContent);
        return new BinaryContentResponse(newBinaryContent.getId(), newBinaryContent.getData(), newBinaryContent.getContentType());
    }

    @Override
    public BinaryContentResponse find(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.find(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        return new BinaryContentResponse(binaryContent.getId(), binaryContent.getData(), binaryContent.getContentType());
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIDs) {
        if (contentIDs.isEmpty()) {
            return null;
        }

        List<BinaryContent> contents = new ArrayList<>();

        for (UUID contentID : contentIDs) {
            BinaryContent content = binaryContentRepository.find(contentID)
                    .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
            contents.add(content);
        }

        return contents.stream()
                .map(ct -> new BinaryContentResponse(
                        ct.getId(),
                        ct.getData(),
                        ct.getContentType()
                ))
                .toList();
    }

    @Override
    public void delete(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.find(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        binaryContentRepository.delete(binaryContent.getId());
    }
}
