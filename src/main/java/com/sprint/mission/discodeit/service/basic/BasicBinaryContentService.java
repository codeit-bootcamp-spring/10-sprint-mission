package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
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

    @Override
    public BinaryContentResponseDTO create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO) {
        byte[] content = binaryContentCreateRequestDTO.content();
        String contentType = binaryContentCreateRequestDTO.contentType();
        BinaryContent binaryContent = new BinaryContent(contentType, content);
        return toBinaryContentResponseDTO(binaryContentRepository.save(binaryContent));
    }

    @Override
    public BinaryContentResponseDTO find(UUID binaryContentId) {
        BinaryContent binaryContent = getBinaryContentByIdOrThrow(binaryContentId);
        return toBinaryContentResponseDTO(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            throw new IllegalArgumentException("입력된 binaryContentIds가 null 또는 빈 리스트 입니다");
        }
        List<BinaryContentResponseDTO> binaryContentResponseDTOList = new ArrayList<>();
        for (UUID binaryContentId : binaryContentIds) {
            BinaryContent binaryContent = getBinaryContentByIdOrThrow(binaryContentId);
            binaryContentResponseDTOList.add(toBinaryContentResponseDTO(binaryContent));
        }
        return binaryContentResponseDTOList;
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(binaryContentId+"를 가진 BinaryContent를 찾지 못했습니다");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

    // 간단한 응답용 DTO를 만드는 메서드
    private BinaryContentResponseDTO toBinaryContentResponseDTO(BinaryContent binaryContent) {
        return new BinaryContentResponseDTO(
                binaryContent.getId(),
                binaryContent.getContent(),
                binaryContent.getContentType()
        );
    }

    // BinaryContentRepository.findById()를 통한 반복되는 BinaryContent 조회/예외처리를 중복제거 하기 위한 메서드
    private BinaryContent getBinaryContentByIdOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("binaryContentId:"+binaryContentId+"를 가진 BinaryContent를 찾지 못했습니다"));
    }
}
