package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.input.BinaryContentCreateInput;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
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

    @Override
    public BinaryContent createBinaryContent(BinaryContentCreateInput input) {
        BinaryContent binaryContent = new BinaryContent(input.contentType(), input.bytes());
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent findBinaryContentById(UUID binaryContentId) {
        return validateAndGetBinaryContentByBinaryContentId(binaryContentId);
    }

    @Override
    public List<BinaryContent> findAllBinaryContentByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) return List.of();

        List<BinaryContent> foundBinaryContentList = new ArrayList<>();
        for (UUID binaryContentId : binaryContentIds) {
            foundBinaryContentList.add(validateAndGetBinaryContentByBinaryContentId(binaryContentId));
        }
        return foundBinaryContentList;
    }

    @Override
    public void deleteBinaryContent(UUID binaryContentId) {
        validateBinaryContentByBinaryContentId(binaryContentId);
        binaryContentRepository.delete(binaryContentId);
    }

    public void validateBinaryContentByBinaryContentId(UUID binaryContentId) {
        ValidationMethods.validateId(binaryContentId);
        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("해당 BinaryContent가 없습니다."));
    }
    public BinaryContent validateAndGetBinaryContentByBinaryContentId(UUID binaryContentId) {
        ValidationMethods.validateId(binaryContentId);
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("해당 BinaryContent가 없습니다."));
    }
}
