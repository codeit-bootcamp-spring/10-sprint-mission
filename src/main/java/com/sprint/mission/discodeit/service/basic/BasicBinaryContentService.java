package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessException;
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
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.id(),
                request.fileName(),
                request.data(),
                request.createAt()
        );
        return convertToResponse(binaryContentRepository.save(binaryContent));
    }

    @Override
    public BinaryContentDto.Response findById(UUID id) {
        return convertToResponse(findBinaryContentById(id));
    }

    @Override
    public BinaryContent findEntityById(UUID id) {
        return findBinaryContentById(id);
    }

    @Override
    public List<BinaryContentDto.Response> findAllIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        findBinaryContentById(id);
        binaryContentRepository.deleteById(id);
    }

    // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
    private BinaryContent findBinaryContentById(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
    }

    private BinaryContentDto.Response convertToResponse(BinaryContent binaryContent) {
        return new BinaryContentDto.Response(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getData(),
                binaryContent.getCreatedAt()
        );
    }
}
