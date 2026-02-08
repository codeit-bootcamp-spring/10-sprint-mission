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
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContent create(BinaryContentCreateDto dto) {
        BinaryContent content = binaryContentRepository.save(binaryContentMapper.toEntity(dto));
        //return binaryContentMapper.toDto(content);
        return content;
    }

    @Override
    public BinaryContent find(UUID id) {
        BinaryContent content = binaryContentRepository.find(id)
                .orElseThrow(()-> new NoSuchElementException("No Such File: "+id));
        //return binaryContentMapper.toDto(content);
        return content;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
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
