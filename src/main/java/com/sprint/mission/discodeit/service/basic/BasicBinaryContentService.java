package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        BinaryContent binaryContent = binaryContentMapper.toEntity(dto);
        binaryContentRepository.save(binaryContent);

        return binaryContent;
    }
    @Override
    public BinaryContent findId(UUID id) {
        return getBinaryContentId(id);
    }

    @Override
    public BinaryContent findBinaryContentByUserId(UUID userId) {

        return getBinaryContentByUserId(userId);
    }

    @Override
    public List<UUID> findAllIdIn() {
        return new ArrayList<>(binaryContentRepository.findAll());
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {

        return binaryContentRepository.findByMessageId(messageId);
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);

    }
    private BinaryContent getBinaryContentByUserId(UUID userId){
        return binaryContentRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 파일컨텐츠가 없습니다."));
    }

    private BinaryContent getBinaryContentId(UUID id){
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 파일컨텐츠가 없습니다."));
    }
}
