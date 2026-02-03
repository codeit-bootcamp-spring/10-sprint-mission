package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.data()
        );
        binaryContentRepository.save(binaryContent);
        return convertToResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        return convertToResponse(getOrThrowBinaryContent(id));
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllById(ids).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        getOrThrowBinaryContent(id);
        binaryContentRepository.deleteById(id);
    }


    // 바이너리 컨텐츠 검증
    private BinaryContent getOrThrowBinaryContent(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 바이너리 콘텐츠를 찾을 수 없습니다."));
    }

    // 엔티티 -> DTO 변환
    private BinaryContentResponse convertToResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getData(),
                binaryContent.getCreatedAt()
        );
    }

}
