package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    // 첨부파일 생성
    BinaryContentResponseDTO create(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO);

    // 첨푸파일 단일 조회
    BinaryContentResponseDTO findById(UUID targetBinaryContentId);

    // 첨부파일 전체 조회
    List<BinaryContentResponseDTO> findAll();

    // 특정 첨부파일 목록 조회
    List<BinaryContentResponseDTO> findByIdIn(List<UUID> targetBinaryContentIds);

    // 첨부파일 삭제
    void delete(UUID targetBinaryContentId);
}
