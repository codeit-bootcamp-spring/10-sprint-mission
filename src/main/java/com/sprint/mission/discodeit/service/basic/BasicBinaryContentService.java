package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.bytes(), request.userId(),request.messageId());
        binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("바이너리컨텐트 객체를 찾을 수 없습니다."));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(binaryContentRepository::findById)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
