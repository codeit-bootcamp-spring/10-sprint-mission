package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BinaryContentDto.BinaryContentResponse create(BinaryContentDto.BinaryContentRequest request){
        Objects.requireNonNull(request.filePath(), "파일 경로를 입력해주세요.");
        Objects.requireNonNull(request.contentType(), "자료 유형을 입력해주세요.");

        BinaryContent binaryContent = new BinaryContent(request);
        return BinaryContentDto.BinaryContentResponse.from(binaryContentRepository.save(binaryContent));
    }

    public BinaryContentDto.BinaryContentResponse findById(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "자료 ID가 유효하지 않습니다.");
        return BinaryContentDto.BinaryContentResponse.from(Objects.requireNonNull(binaryContentRepository.findById(binaryContentId)));
    }

    public List<BinaryContentDto.BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds){
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream().map(BinaryContentDto.BinaryContentResponse::from).toList();
    }

    public void delete(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "유저 Id가 유효하지 않습니다.");
        binaryContentRepository.deleteById(binaryContentId);
    }
}
