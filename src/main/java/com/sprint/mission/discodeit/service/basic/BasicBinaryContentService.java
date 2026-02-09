package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.mapper.BinaryContentDTOMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentDTOMapper binaryContentDTOMapper;

    @Override
    public BinaryContentResponseDTO create(BinaryContentCreateRequestDTO req) {
        Objects.requireNonNull(req, "해당 요청은 유효하지 않습니다!");
        Objects.requireNonNull(req.contentType(), "요청의 컨텐트 타입이 유효하지 않습니다!");
        Objects.requireNonNull(req.file(), "요청의 내용이 유효하지 않습니다!");

        BinaryContent saved = binaryContentRepository.save(binaryContentDTOMapper.requestToBinaryContent(req));

        return binaryContentDTOMapper.binaryContentToResponse(saved);
    }

    @Override
    public ResponseEntity<BinaryContent> find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        BinaryContent binaryContent = binaryContentRepository.findbyId(id).orElseThrow(() -> new NoSuchElementException("해당 첨부파일을 찾지 못했습니다."));
        return ResponseEntity.ok(binaryContent);
    }



    @Override
    public List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll()
                .stream()
                .filter(bc -> ids.contains(bc.getId()))
                .map(binaryContentDTOMapper::binaryContentToResponse).toList();
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "해당 ID는 유효하지 않습니다!");
        binaryContentRepository.deleteByID(id); // delete 메소드 내부에서 영속화 진행
    }

}
