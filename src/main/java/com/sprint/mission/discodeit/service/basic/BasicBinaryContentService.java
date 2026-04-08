package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.InternalDiscodeitException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.ImageBinaryConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        log.debug("파일 저장 처리 시작: fileName={}, size={}", request.file().getOriginalFilename(),
                request.file().getSize());
        BinaryContent binaryContent = new BinaryContent(
                request.file().getOriginalFilename(),
                request.file().getSize(),
                request.file().getContentType()
        );
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        byte[] bytes = ImageBinaryConverter.convert(request.file());
        UUID storedId = binaryContentStorage.put(saved.getId(), bytes);
        validateStoredId(saved.getId(), storedId);
        log.info("파일 저장 완료: binaryContentId={}, fileName={}", saved.getId(), saved.getFileName());

        return saved.getId();
    }

    @Override
    @Transactional
    public List<BinaryContent> createBinaryContents(List<BinaryContentRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        log.debug("다중 파일 저장 처리 시작: count={}", requests.size());
        List<BinaryContent> binaryContents = new ArrayList<>();

        for (BinaryContentRequest request : requests) {
            UUID binaryContentId = createBinaryContent(request);
            if (binaryContentId == null) {
                continue;
            }
            binaryContents.add(getBinaryContentOrThrow(binaryContentId));
        }
        log.info("다중 파일 저장 완료: 저장된 파일 수={}", binaryContents.size());

        return binaryContents;
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findBinaryContent(UUID binaryContentId) {
        log.debug("파일 조회: binaryContentId={}", binaryContentId);
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
        log.debug("다중 파일 조회: count={}", binaryContentIds.size());
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> downloadBinaryContent(UUID binaryContentId) {
        log.info("파일 다운로드 처리: binaryContentId={}", binaryContentId);
        BinaryContentDto binaryContentDto = findBinaryContent(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);
    }

    @Override
    @Transactional
    public void deleteBinaryContent(UUID binaryContentId) {
        log.debug("파일 삭제 처리 시작: binaryContentId={}", binaryContentId);
        BinaryContent binaryContent = getBinaryContentOrThrow(binaryContentId);
        binaryContentRepository.delete(binaryContent);
        log.info("파일 삭제 완료: binaryContentId={}", binaryContentId);
    }

    private void validateStoredId(UUID savedId, UUID storedId) {
        if (!savedId.equals(storedId)) {
            Map<String, Object> details = new java.util.LinkedHashMap<>();
            details.put("binaryContentId", savedId);
            if (storedId != null) {
                details.put("storedId", storedId);
            }
            throw new InternalDiscodeitException(
                    "BinaryContent 저장 키가 일치하지 않습니다. binaryContentId: " + savedId,
                    details
            );
        }
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(
                        "BinaryContent 찾을 수 없습니다 binaryContentId: " + binaryContentId,
                        Map.of("binaryContentId", binaryContentId)
                ));
    }
}
