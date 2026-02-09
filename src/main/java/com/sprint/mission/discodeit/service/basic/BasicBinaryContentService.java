package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.function.ThrowingFunction;
import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreation;
import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService extends BasicDomainService<BinaryContent> implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .filter(binaryContentRepository::existsById)
                .map(ThrowingFunction.unchecked(this::find))
                .toList();
    }

    @Override
    public BinaryContentResponse create(BinaryContentCreation model) throws IOException {
        BinaryContent content = new BinaryContent(model.fileName(), model.fileType(), model.data());
        binaryContentRepository.save(content);
        return content.toResponse();
    }

    @Override
    public BinaryContentResponse find(UUID id) throws IOException, ClassNotFoundException {
        return findById(id).toResponse();
    }

    @Override
    public void delete(UUID id) throws IOException {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("Binary Content with id, %s, not found".formatted(id));
        }
        binaryContentRepository.deleteById(id);
    }

    @Override
    protected BinaryContent findById(UUID id) throws IOException, ClassNotFoundException {
        return findEntityById(id, "BinaryContent", binaryContentRepository);
    }
}
