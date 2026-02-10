package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentDto create(BinaryContentCreateRequest binaryContentCreateRequest) {
        String fileName = binaryContentCreateRequest.fileName();
        byte[] bytes = binaryContentCreateRequest.bytes();
        String contentType = binaryContentCreateRequest.contentType();

        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType,
                bytes
        );
        binaryContentRepository.save(binaryContent);

        return toDto(binaryContent);
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new BusinessException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
        }

        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType()
        );
    }
}
