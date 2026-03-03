package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentServiceDTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .map(this::find)
                .toList();
    }

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(request.fileName(), request.data());
        binaryContentRepository.save(content);
        return content.toResponse();
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        return findById(id).toResponse();
    }

    @Override
    public void delete(UUID id) {
        deleteIfExist(id, binaryContentRepository, () -> new APIException(ErrorCode.BINARYCONTENTID_NOT_FOUND, id));
    }

    @Override
    protected BinaryContent findById(UUID id) {
        return findEntityById(id, binaryContentRepository,
                () -> new APIException(ErrorCode.BINARYCONTENTID_NOT_FOUND, id));
    }
}
