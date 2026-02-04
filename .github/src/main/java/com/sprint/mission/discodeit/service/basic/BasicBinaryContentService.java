package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
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
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        BinaryContent content = new BinaryContent(
                request.bytes(),
                request.contentType(),
                request.fileName()
        );
        return convertToResponse(binaryContentRepository.save(content));
    }

    @Override
    public BinaryContentDto.Response find(UUID userId) {
        return binaryContentRepository.findById(userId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 파일이 존재하지 않습니다."));
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIdIn(List<UUID> idList) {
        return binaryContentRepository.findAllById(idList).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("해당 ID의 파일이 존재하지 않습니다.");
        }
        binaryContentRepository.deleteById(id);
    }

    private BinaryContentDto.Response convertToResponse(BinaryContent content) {
        return new BinaryContentDto.Response(
                content.getId(),
                content.getBytes(),
                content.getContentType(),
                content.getFileName()
        );
    }
}
