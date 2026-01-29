package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    public final BinaryContentRepository binaryContentRepository;


    @Override
    public BinaryContent create(BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        return binaryContentRepository.save(new BinaryContent(binaryContentCreateRequestDto.content()));
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new AssertionError("BinaryContent not found"));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> idList) {
        List<BinaryContent> binaryContents = new ArrayList<>();

        idList.forEach(id -> binaryContents.add(binaryContentRepository.findById(id)
                .orElseThrow(() -> new AssertionError("BinaryContent not found"))));

        return binaryContents;

    }

    @Override
    public void delete(UUID binaryContentId) {
        binaryContentRepository.deleteById(binaryContentId);
    }
}
