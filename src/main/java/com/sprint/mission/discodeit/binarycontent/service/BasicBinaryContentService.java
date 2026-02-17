package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;


    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.bytes(), request.userId(),request.messageId());
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.convertToResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("바이너리컨텐트 객체를 찾을 수 없습니다."));
        return binaryContentMapper.convertToResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(binaryContentRepository::findById)
                .flatMap(Optional::stream)
                .map(binaryContentMapper::convertToResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
