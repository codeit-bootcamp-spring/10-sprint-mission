package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
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
    public BinaryContentDto.response create(BinaryContentDto.createRequest createReq) {
        return null;
    }

    @Override
    public BinaryContentDto.response findById(UUID uuid) {
        return binaryContentRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 binaryContent입니다"));
    }

    @Override
    public List<BinaryContentDto.response> findAllByIdIn(List<UUID> uuids) {
        return binaryContentRepository.findAllByIdIn(uuids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        binaryContentRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 binaryContent입니다"));
        binaryContentRepository.deleteById(uuid);
    }

    private BinaryContentDto.response toResponse(BinaryContent binaryContent) {
        return new BinaryContentDto.response(binaryContent.getId(), binaryContent.getCreatedAt(),
                binaryContent.getContentType(), binaryContent.getFileName(), binaryContent.getContentBytes());
    }
}
