package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper mapper;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request){
        request.validate();
        BinaryContent bc = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        binaryContentRepository.save(bc);
        return mapper.toResponse(bc);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        BinaryContent bc = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent가 없습니다."));

        return mapper.toResponse(bc);
    }
    @Override
    public List<BinaryContentResponse> findAll(List<UUID> ids) {
        if (ids == null) throw new IllegalArgumentException("ids는 null일 수 없습니다.");

        // 빈 리스트면 빈 결과 반환 (일반적으로 자연스러움)
        if (ids.isEmpty()) return List.of();

        return ids.stream()
                .map(this::findById) // findById가 예외처리+매핑까지 해줌
                .toList();
    }
    @Override
    public void delete(UUID id) {
        if (id==null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        binaryContentRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("삭제할 BinaryContent가 없습니다."));
        binaryContentRepository.delete(id);
    }



}
