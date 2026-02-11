package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService{
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    public BinaryContentResponse create(BinaryContentRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.getContent(),
                request.getFileName(),
                request.getContentType()
        );
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toResponse(binaryContent);
    }

    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨텐츠입니다."));
        return binaryContentMapper.toResponse(binaryContent);
    }

    public BinaryContent findContent(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨텐츠입니다."));
    }

    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(binaryContentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}
