package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
    //
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] content = request.content();

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, content);
        return binaryContentMapper.toResponse(binaryContentRepository.save(binaryContent));
    }

    @Override
    public BinaryContentDto.Response find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다: " + binaryContentId));
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIn(binaryContentIds).stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("해당 파일을 찾을 수 없습니다: " + binaryContentId);
        }

        binaryContentRepository.deleteById(binaryContentId);
    }
}
