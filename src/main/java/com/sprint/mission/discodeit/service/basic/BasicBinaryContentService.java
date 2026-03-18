package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.ImageBinaryConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public UUID createBinaryContent(BinaryContentRequest request) {
        if (request == null || request.file() == null || request.file().isEmpty()) {
            return null;
        }

        BinaryContent binaryContent = new BinaryContent(
                request.file().getOriginalFilename(),
                request.file().getSize(),
                request.file().getContentType()
        );
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        byte[] bytes = ImageBinaryConverter.convert(request.file());
        UUID storedId = binaryContentStorage.put(saved.getId(), bytes);
        validateStoredId(saved.getId(), storedId);

        return saved.getId();
    }

    @Override
    @Transactional
    public List<BinaryContent> createBinaryContents(List<BinaryContentRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = new ArrayList<>();

        for (BinaryContentRequest request : requests) {
            UUID binaryContentId = createBinaryContent(request);
            if (binaryContentId == null) {
                continue;
            }
            binaryContents.add(getBinaryContentOrThrow(binaryContentId));
        }

        return binaryContents;
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findBinaryContent(UUID binaryContentId) {
        return binaryContentMapper.toDto(getBinaryContentOrThrow(binaryContentId));
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContent findBinaryContentEntity(UUID binaryContentId) {
        return getBinaryContentOrThrow(binaryContentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> downloadBinaryContent(UUID binaryContentId) {
        BinaryContentDto binaryContentDto = findBinaryContent(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);
    }

    @Override
    @Transactional
    public void deleteBinaryContent(UUID binaryContentId) {
        BinaryContent binaryContent = getBinaryContentOrThrow(binaryContentId);
        binaryContentRepository.delete(binaryContent);
    }

    private void validateStoredId(UUID savedId, UUID storedId) {
        if (!savedId.equals(storedId)) {
            throw new ApiException(
                    ErrorCode.INTERNAL_ERROR,
                    "BinaryContent 저장 키가 일치하지 않습니다. binaryContentId: " + savedId
            );
        }
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new ApiException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
                        "BinaryContent 찾을 수 없습니다 binaryContentId: " + binaryContentId));
    }
}
