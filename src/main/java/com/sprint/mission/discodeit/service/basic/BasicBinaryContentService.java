package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentDto create(CreateBinaryContentRequestDto request) {
        log.info("첨부 파일 업로드 요청: fileName={}, size={}", request.fileName(), request.size());

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size()
        );
        binaryContent = binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), request.contents());

        log.info("첨부 파일 업로드 완료: contentId={}", binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID contentId) {
        BinaryContent binaryContent = getBinaryContentEntity(contentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> contentIds){
        return binaryContentRepository.findAllById(contentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    public BinaryContent findEntity(UUID contentId) {
        return getBinaryContentEntity(contentId);
    }

    @Override
    @Transactional
    public void delete(UUID contentId) {
        log.warn("첨부 파일 삭제 요청: contentId={}", contentId);
        BinaryContent binaryContent = getBinaryContentEntity(contentId);
        binaryContentRepository.delete(binaryContent);
        log.info("첨부 파일 삭제 완료: contentId={}", contentId);
    }

    // ------ 내부 메서드 -------
    private BinaryContent getBinaryContentEntity(UUID contentId) {
        return binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found with id " + contentId));
    }
}
