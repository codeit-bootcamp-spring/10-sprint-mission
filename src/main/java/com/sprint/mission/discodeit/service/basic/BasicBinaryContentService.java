package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(CreateBinaryContentRequestDTO dto) {
        validateCreateRequest(dto);
        CreateBinaryContentPayloadDTO payload
                = new CreateBinaryContentPayloadDTO(dto.data(), dto.contentType(), dto.filename(), dto.data().length);

        BinaryContent binaryContent = binaryContentMapper.toEntity(payload);
        binaryContentRepository.save(binaryContent);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findById(UUID binaryContentId) {
        BinaryContent binaryContent = findBinaryContentOrThrow(binaryContentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        Objects.requireNonNull(ids, "id 리스트는 null값일 수 없습니다.");

        if (ids.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(ids);

        if (ids.size() != binaryContents.size()) {
            throw new NoSuchElementException(
                    "요청한 BinaryContent id 중 일부가 존재하지 않습니다. 요청: " + ids.size()
                            + "건, 조회: " + binaryContents.size() + "건"
                    );
        }

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAll() {
        List<BinaryContent> binaryContents = binaryContentRepository.findAll();

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Override
    public void delete(UUID binaryContentId) {
        findBinaryContentOrThrow(binaryContentId);

        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContent findBinaryContentOrThrow(UUID binaryContentId) {
        Objects.requireNonNull(binaryContentId, "binaryContentId는 null값일 수 없습니다.");

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("해당 id에 binaryContent가 존재하지 않습니다."));

        return binaryContent;
    }

    private void validateCreateRequest(CreateBinaryContentRequestDTO dto) {
        Objects.requireNonNull(dto, "dto는 null값일 수 없습니다.");

        if (dto.userId() == null) {
            throw new IllegalArgumentException("userId는 null값일 수 없습니다.");
        }

        if (dto.data() == null || dto.data().length == 0) {
            throw new IllegalArgumentException("data는 null/empty값일 수 없습니다.");
        }

        if (dto.contentType() == null || dto.contentType().isBlank()) {
            throw new IllegalArgumentException("contentType은 null/empty값일 수 없습니다.");
        }

        if (dto.filename() == null || dto.filename().isBlank()) {
            throw new IllegalArgumentException("filename은 null/empty값일 수 없습니다.");
        }
    }
}
