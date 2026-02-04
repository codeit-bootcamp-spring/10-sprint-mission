package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentRequestCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponseDto create(BinaryContentRequestCreateDto request) {
        Validators.requireNonNull(request, "request");
        validateBinaryContent(request.data(), request.contentType());

        BinaryContent binaryContent = new BinaryContent(request.data(), request.contentType());
        return toDto(binaryContentRepository.save(binaryContent));
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent binaryContent = validateExistenceBinaryContent(id);
        return toDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        Validators.requireNonNull(ids, "ids");
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .map(BasicBinaryContentService::toDto)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        validateExistenceBinaryContent(id);
        binaryContentRepository.deleteById(id);
    }


    private void validateBinaryContent(byte[] data, String contentType) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("첨부파일 데이터가 비어있습니다.");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("첨부파일 contentType이 비어있습니다.");
        }
    }

    private BinaryContent validateExistenceBinaryContent(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent가 존재하지 않습니다."));

    }

    public static BinaryContentResponseDto toDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getData(),
                binaryContent.getContentType()
        );
    }
}
