package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.FileProcessingException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentDto.CreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw FileProcessingException.readFailed(file.getOriginalFilename(), e);
        }
    }

    @Override
    @Transactional
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes.length);

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(savedBinaryContent.getId(), bytes);

        return binaryContentMapper.toResponse(savedBinaryContent);
    }

    @Override
    public BinaryContentDto.Response find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds) {
        Set<UUID> uniqueIds = new HashSet<>(binaryContentIds);
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(uniqueIds);

        if (binaryContents.size() != uniqueIds.size()) {
            Set<UUID> foundIds = binaryContents.stream()
                    .map(BinaryContent::getId)
                    .collect(Collectors.toSet());

            List<UUID> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw BinaryContentNotFoundException.withIds(uniqueIds.size(), missingIds);
        }

        return binaryContents.stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        binaryContentRepository.delete(binaryContent);
    }
}
