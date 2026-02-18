package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(CreateBinaryContentRequestDto request) {
        BinaryContent binaryContent = new BinaryContent(
                request.getFileName(),
                request.getContentType(),
                request.getSize(),
                request.getContents()
        );
        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentDto find(UUID contentId) {
        BinaryContent binaryContent = getBinaryContentEntity(contentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> contentIds){
        return contentIds.stream()
                .map(binaryContentRepository::findById) // 1개씩 조회
                .filter(Optional::isPresent)            // 없으면 스킵
                .map(Optional::get)
                .map(binaryContentMapper::toDto)        // DTO 변환
                .toList();
    }

    @Override
    public BinaryContent findEntity(UUID contentId) {
        return getBinaryContentEntity(contentId);
    }

    @Override
    public void delete(UUID contentId) {
        getBinaryContentEntity(contentId);
        binaryContentRepository.deleteById(contentId);
    }

    // ------ 내부 메서드 -------
    private BinaryContent getBinaryContentEntity(UUID contentId) {
        return binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found with id " + contentId));
    }
}
