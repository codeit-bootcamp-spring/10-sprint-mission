package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper mapper;

    @Override
    public BinaryContentDto create(MultipartFile attachment) throws IOException {
        if (attachment == null) return null;

        BinaryContent content = new BinaryContent(attachment.getOriginalFilename(),
                attachment.getSize(), attachment.getContentType());
        binaryContentRepository.save(content);
        binaryContentStorage.put(content.getId(), attachment.getBytes());
        return toResponse(content);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto findById(UUID uuid) {
        return binaryContentRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> uuids) {
        return binaryContentRepository.findAllByIdIn(uuids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) throws IOException{
        binaryContentRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));
        binaryContentStorage.delete(uuid);
        binaryContentRepository.deleteById(uuid);
    }

    private BinaryContentDto toResponse(BinaryContent binaryContent) {
        return mapper.toDto(binaryContent);
    }
}
