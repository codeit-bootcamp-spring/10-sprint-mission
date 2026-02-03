package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto dto) {
        BinaryContent content = new BinaryContent(
                dto.getBytes(),
                dto.getFilename(),
                dto.getContentType()
        );
        binaryContentRepository.save(content);
        return convertToDto(content);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        return binaryContentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }

    private BinaryContentResponseDto convertToDto(BinaryContent content) {
        return new BinaryContentResponseDto(
                content.getId(),
                content.getBytes(),
                content.getFileName(),
                content.getContentType()
        );
    }
}