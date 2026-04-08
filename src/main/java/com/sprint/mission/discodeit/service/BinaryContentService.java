package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

//    public BinaryContent create(BinaryContentPostDto binaryContentPostDto) {
//        return binaryContentRepository.save(binaryContentMapper.toEntity(binaryContentPostDto));
//    }

    @Transactional(readOnly = true)
    public BinaryContentDto findById(UUID id) throws IOException {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND, id)
            );

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> idList) {
        return binaryContentRepository.findByIdIn(idList).stream()
            .map(binaryContentMapper::toDto)
            .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND, id);
        }

        binaryContentRepository.deleteById(id);
    }
}
