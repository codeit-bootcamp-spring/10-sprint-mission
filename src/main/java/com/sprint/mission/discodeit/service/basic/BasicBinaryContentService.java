package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentDeleteException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();

        try {
            BinaryContent binaryContent = new BinaryContent(
                    fileName,
                    (long) bytes.length,
                    contentType
            );
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), bytes);

            log.info("[BINARY_CONTENT_CREATE] 파일 생성 완료: binaryContentId={}, fileName={}",
                    binaryContent.getId(), fileName);
            return binaryContentMapper.toDto(binaryContent);
        } catch (Exception e) {
            throw new BinaryContentSaveException(fileName, e);
        }
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toDto)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new BinaryContentNotFoundException(binaryContentId);
        }

        try {
            binaryContentRepository.deleteById(binaryContentId);
            log.info("[BINARY_CONTENT_DELETE] 파일 삭제 완료: binaryContentId={}", binaryContentId);
        } catch (Exception e) {
            throw new BinaryContentDeleteException(binaryContentId, e);
        }
    }
}
