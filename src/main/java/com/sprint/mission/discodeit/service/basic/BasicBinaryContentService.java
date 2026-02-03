package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        // 바이너리 컨텐츠 생성을 위한 필수 검증
        if (request == null || request.fileName() == null || request.contentType() == null || request.bytes() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 바이너리 컨텐츠 생성 및 저장
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
        binaryContentRepository.save(binaryContent);

        return BinaryContentResponse.from(binaryContent);
    }

    public BinaryContentResponse findById(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 바이너리 컨텐츠가 존재하는지 검색 및 검증
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new RuntimeException("바이너리 컨텐츠가 존재하지 않습니다."));

        return BinaryContentResponse.from(binaryContent);
    }

    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // ID 목록 검증
        for (UUID binaryContentId : binaryContentIds) {
            if (binaryContentId == null) {
                throw new RuntimeException("바이너리 컨텐츠가 존재하지 않습니다.");
            }
        }

        List<BinaryContentResponse> responses = new ArrayList<>();
        // 바이너리 컨텐츠 목록 조회 및 순회
        for (BinaryContent binaryContent : binaryContentRepository.findAllByIdIn(binaryContentIds)) {
            // 응답 DTO 생성 후 반환 리스트에 추가
            responses.add(BinaryContentResponse.from(binaryContent));
        }

        return responses;
    }

    public void delete(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 바이너리 컨텐츠가 존재하는지 검증
        binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new RuntimeException("바이너리 컨텐츠가 존재하지 않습니다."));

        binaryContentRepository.delete(binaryContentId);
    }
}
