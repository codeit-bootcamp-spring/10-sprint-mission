package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
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

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto dto) {
        BinaryContent content = binaryContentRepository.save(BinaryContentMapper.toEntity(dto));
        return BinaryContentMapper.toDto(content);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent content = binaryContentRepository.find(id)
                .orElseThrow(()-> new NoSuchElementException("No Such File: "+id));
        return BinaryContentMapper.toDto(content);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        return ids.stream().map(this::find).toList();
    }

    @Override
    public void delete(UUID id) {
        if(!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("No Such File: "+id);
        }
        binaryContentRepository.delete(id);
    }
}
