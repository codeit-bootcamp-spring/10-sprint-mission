package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentInfoDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentInfoDto create(BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent binaryContent = new BinaryContent(binaryContentCreateDto.content());
        binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toBinaryContentInfoDto(binaryContent);
    }

    @Override
    public BinaryContentInfoDto find(UUID id) {
        return binaryContentMapper.toBinaryContentInfoDto(binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다.")));
    }

    @Override
    public List<BinaryContentInfoDto> findAllByIdIn(List<UUID> idList) {
        return binaryContentRepository.findAllByIdIn(idList).stream()
                .map(binaryContentMapper::toBinaryContentInfoDto)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다."));
        binaryContentRepository.delete(id);
    }
}
