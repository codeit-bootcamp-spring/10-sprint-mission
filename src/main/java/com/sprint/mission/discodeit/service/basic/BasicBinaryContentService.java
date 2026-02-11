package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        binaryContentRepository.save(binaryContent);

        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 첨부파일(BinaryContent)을 찾을 수 없습니다. id: " + id));

        return toResponse(binaryContent);
    }

    // 여러 개의 id 목록을 받아, 실제로 존재하는 바이너리 콘텐츠들만 조회
    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(binaryContentRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (binaryContentRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("삭제할 첨부파일(BinaryContent)을 찾을 수 없습니다. id: " + id);
        }
        binaryContentRepository.deleteById(id);
    }

    @Override
    public BinaryContent findContent(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 첨부파일(BinaryContent)을 찾을 수 없습니다. id: " + id));
    }

    // ===

    // Entity -> DTO 변환 로직
    private BinaryContentResponse toResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize(),
                binaryContent.getBytes()
        );
    }
}
