package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Transactional
    @Override
    public BinaryContentDto find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        BinaryContent binaryContent = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 첨부파일을 찾지 못했습니다."));

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        Objects.requireNonNull(ids, "유효하지 않은 식별자 목록!");
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto)
            .toList();

    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "해당 ID는 유효하지 않습니다!");
        binaryContentRepository.deleteById(id);
    }

}
