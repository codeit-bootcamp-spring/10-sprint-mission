package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFound;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDTO create(BinaryContentCreateRequest request) {
        // binaryContent 새로 생성
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                (long) request.bytes().length
        );
        // save -> binaryContent가 영속성 컨텍스트에 등록 및 이때 Id 부여
        binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(binaryContent.getId(), request.bytes());

        log.info("파일 업로드 성공 - fileId: {}, fileName: {}", binaryContent.getId(), binaryContent.getFileName());
        return binaryContentMapper.toDTO(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDTO find(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new BinaryContentNotFound(contentID));
        log.debug("파일 단건 조회 성공 - fileId: {}", binaryContent.getId());
        return binaryContentMapper.toDTO(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDTO> findAllByIdIn(List<UUID> contentIDs) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(contentIDs);
        log.debug("파일 다건 조회 성공 - contentIds: {}", contentIDs);
        return binaryContents.stream()
                .map(binaryContentMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new BinaryContentNotFound(contentID));
        binaryContentRepository.delete(binaryContent);
        log.info("파일 삭제 성공 - contentID: {}", contentID);
    }

    @Override
    @Transactional
    public ResponseEntity<?> download(UUID binaryContentID){
        BinaryContent bt = binaryContentRepository.findById(binaryContentID)
                .orElseThrow(() -> new BinaryContentNotFound(binaryContentID));

        BinaryContentDTO dto = binaryContentMapper.toDTO(bt);
        log.debug("파일 다운로드 성공 - contentId: {}", binaryContentID);
        return binaryContentStorage.download(dto);
    }
}
