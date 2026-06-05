package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequest dto) {
        BinaryContent binaryContent = new BinaryContent(
                dto.getSize(),
                dto.getName(),
                dto.getContentType());

        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toDto(binaryContent);
    }


    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findBinaryContent(UUID id) {
        return  binaryContentMapper.toDto(getBinaryContent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllIdIn(List<UUID> binaryContentIds) {
        // Jpa에 리스트를 받으면 요소들에 해당하는 객체를 리스트로 반환해줌
        return binaryContentRepository.findAllById(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent binaryContent = getBinaryContent(binaryContentId);
        binaryContent.updateStatus(status);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);

    }

    private BinaryContent getBinaryContent(UUID id){
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(id));
    }
}
