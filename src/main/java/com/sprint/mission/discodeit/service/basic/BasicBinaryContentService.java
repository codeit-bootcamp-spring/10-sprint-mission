package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    // 첨부 파일 생성
    @Override
    public BinaryContentResponseDTO create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO) {
        BinaryContentEntity newBinaryContent = new BinaryContentEntity(binaryContentCreateRequestDTO);
        binaryContentRepository.save(newBinaryContent);

        return toResponseDTO(newBinaryContent);
    }

    // 첨부 파일 단건 조회
    @Override
    public BinaryContentResponseDTO findById(UUID targetBinaryContentId) {
        BinaryContentEntity targetBinaryContent = findEntityById(targetBinaryContentId);

        return toResponseDTO(targetBinaryContent);
    }

    // 첨부 파일 다건 조회
    @Override
    public List<BinaryContentResponseDTO> findAllByIds(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContentEntity -> ids.contains(binaryContentEntity.getId()))
                .map(this::toResponseDTO)
                .toList();
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContentResponseDTO> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 첨부 파일 목록 조회
    @Override
    public List<BinaryContentResponseDTO> findByIdIn(List<UUID> targetBinaryContentIds) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContent -> targetBinaryContentIds.contains(binaryContent.getId()))
                .map(this::toResponseDTO)
                .toList();
    }

    // 첨부 파일 삭제
    @Override
    public void delete(UUID targetBinaryContentId) {
        BinaryContentEntity targetBinaryContent = findEntityById(targetBinaryContentId);
        binaryContentRepository.delete(targetBinaryContent);
    }

    @Override
    // 단일 엔티티 조회
    public BinaryContentEntity findEntityById(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 첨부 파일이 존재하지 않습니다."));
    }

    // 응답 DTO 생성 및 반환
    public BinaryContentResponseDTO toResponseDTO(BinaryContentEntity binaryContent) {
        return BinaryContentResponseDTO.builder()
                .id(binaryContent.getId())
                .createdAt(binaryContent.getCreatedAt())
                .fileName(binaryContent.getFileName())
                .binaryContent(binaryContent.getBytes())
                .binaryContentType(binaryContent.getContentType())
                .build();
    }
}
