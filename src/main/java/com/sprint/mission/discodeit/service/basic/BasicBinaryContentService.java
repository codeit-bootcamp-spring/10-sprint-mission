package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
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
    public BinaryContentResponseDto create(BinaryContentCreateDto dto) {
        BinaryContent binaryContent = new BinaryContent(dto.getUserId(),
                dto.getMessageId(),
                dto.getFileData(),
                dto.getName(),
                dto.getFileType());
        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public BinaryContentResponseDto findBinaryContentByUserId(UUID userId) {

        return binaryContentMapper.toDto(getBinaryContent(userId));
    }

    @Override
    public List<BinaryContentResponseDto> findAllByMessageId(UUID messageId) {

        return binaryContentRepository.findByMessageId(messageId).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);

    }
    private BinaryContent getBinaryContent(UUID userId){
        return binaryContentRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 파일컨텐츠가 없습니다."));
    }
}
