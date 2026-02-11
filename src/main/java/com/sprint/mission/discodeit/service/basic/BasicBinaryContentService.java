package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.status.BinaryContent;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContentResponse create(CreateBinaryContentRequest request) {
        BinaryContent content = new BinaryContent(
                UUID.randomUUID(),
                request.getContentType(),
                request.getData().length,
                request.getData(),
                request.getFileName(),
                Instant.now(),
                Instant.now(),
                request.getAttachmentid()
        );

        //저장
        BinaryContent saved = binaryContentRepository.save(content);
        return BinaryContentResponse.from(saved);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "바이너리 콘텐츠가 발견되지 못하였습니다. id :" + id
                ));

        return BinaryContentResponse.from(content);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids){
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(BinaryContentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public  void delete(UUID id){
        // 존재 여부 확인
        if(!binaryContentRepository.existsById(id)){
            throw  new NoSuchElementException(
              "바이너리 콘텐츠가 발견되지 못하였습니다. id :" + id
            );
        }
        // 삭제
        binaryContentRepository.deleteById(id);
    }


}
