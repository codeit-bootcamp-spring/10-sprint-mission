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
    public BinaryContentDto.Response create(BinaryContentDto.Create request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.size(),
                request.bytes()
        );
        binaryContentRepository.save(binaryContent);

        return BinaryContentDto.Response.of(binaryContent);
    }

    @Override
    public BinaryContentDto.Response findById(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다."));
        return BinaryContentDto.Response.of(binaryContent);
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIdIn(List<UUID> contentsIds) {
        return contentsIds.stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다."));
        binaryContentRepository.delete(binaryContent);
    }
}
