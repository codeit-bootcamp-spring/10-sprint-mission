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
        // 컨텐츠 생성을 위한 필수 검증
        if (request == null || request.fileName() == null || request.contentType() == null || request.bytes() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 컨텐츠 생성 및 저장
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
        binaryContentRepository.save(binaryContent);

        return BinaryContentResponse.from(binaryContent);
    }

    public BinaryContentResponse findById(UUID contentId) {
        if (contentId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 컨텐츠가 존재하는지 검색 및 검증
        BinaryContent binaryContent = binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("컨텐츠가 존재하지 않습니다."));

        return BinaryContentResponse.from(binaryContent);
    }

    public List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIds) {
        if (contentIds == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // ID 목록 검증
        for (UUID contentId : contentIds) {
            if (contentId == null) {
                throw new RuntimeException("컨텐츠가 존재하지 않습니다.");
            }
        }

        List<BinaryContentResponse> responses = new ArrayList<>();
        // 컨텐츠 목록 조회 및 순회
        for (BinaryContent content : binaryContentRepository.findAllByIdIn(contentIds)) {
            // 응답 DTO 생성 후 반환 리스트에 추가
            responses.add(BinaryContentResponse.from(content));
        }

        return responses;
    }

    public void delete(UUID contentId) {
        if (contentId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 컨텐츠가 존재하는지 검증
        binaryContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("컨텐츠가 존재하지 않습니다."));

        binaryContentRepository.delete(contentId);
    }
}
