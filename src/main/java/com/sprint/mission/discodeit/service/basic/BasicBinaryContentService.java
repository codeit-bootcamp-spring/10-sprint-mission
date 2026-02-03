package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final EntityFinder entityFinder;

    public BinaryContentDto.BinaryContentResponse create(BinaryContentDto.BinaryContentRequest request){
        Objects.requireNonNull(request.filePath(), "파일 경로를 입력해주세요.");
        Objects.requireNonNull(request.contentType(), "자료 유형을 입력해주세요.");

        BinaryContent binaryContent = new BinaryContent(request);
        return BinaryContentDto.BinaryContentResponse.from(binaryContentRepository.save(binaryContent));
    }

    public BinaryContentDto.BinaryContentResponse findById(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "자료 ID가 유효하지 않습니다.");
        return BinaryContentDto.BinaryContentResponse.from(entityFinder.getBinaryContent(binaryContentId));
    }

    public List<BinaryContentDto.BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds){
        Objects.requireNonNull(binaryContentIds, "자료 ID목록이 유효하지 않습니다.");
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream().map(BinaryContentDto.BinaryContentResponse::from).toList();
    }

    public void delete(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "자료 Id가 유효하지 않습니다.");
        binaryContentRepository.deleteById(binaryContentId);
    }
}
