package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final EntityFinder entityFinder;

    @Override
    public BinaryContentDto.BinaryContentResponse create(BinaryContentDto.BinaryContentRequest request){
        BinaryContent binaryContent = new BinaryContent(request);
        return BinaryContentDto.BinaryContentResponse.from(binaryContentRepository.save(binaryContent));
    }

    @Override
    public BinaryContentDto.BinaryContentResponse findById(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "자료 ID가 유효하지 않습니다.");
        return BinaryContentDto.BinaryContentResponse.from(entityFinder.getBinaryContent(binaryContentId));
    }

    @Override
    public List<BinaryContentDto.BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds){
        Objects.requireNonNull(binaryContentIds, "자료 ID목록이 유효하지 않습니다.");
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream().map(BinaryContentDto.BinaryContentResponse::from).toList();
    }

    @Override
    public void delete(UUID binaryContentId){
        Objects.requireNonNull(binaryContentId, "자료 Id가 유효하지 않습니다.");
        binaryContentRepository.deleteById(binaryContentId);
    }
}
