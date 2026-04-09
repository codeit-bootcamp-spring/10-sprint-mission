package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.FieldNotValidException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Transactional
    @Override
    public BinaryContentDto find(UUID id) {
        // id null 체크
        if (id == null) {
            throw new FieldNotValidException("id");
        }

        // Id로 BinaryContent 조회 메서드 시작 로그
        log.trace("[BinaryContent] BinaryContent 조회 메서드 시작: id={}", id);

        // BinaryContent 레포지토리에서 BinaryContent 조회
        BinaryContent binaryContent = getBinaryContent(id);

        log.debug("[BinaryContent] 조회된 BinaryContent 정보: id={}, fileName={}, size={}",
            binaryContent.getId(), binaryContent.getFileName(), binaryContent.getSize());

        log.info("[BinaryContent] BinaryContent 조회 성공");
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        // id 리스트 null 체킹
        if (ids == null) {
            throw new FieldNotValidException("ids");
        }

        log.trace("[BinaryContent] ID 리스트로 BinaryContent 조회 메서드 시작: ids={}", ids);

        // 첨부 파일 ID 리스트 기반으로 첨부 파일 DTO 생성
        List<BinaryContentDto> result = binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto)
            .toList();

        // 첫 번째 첨부파일의 Id만 로그로 찍음.
        log.info("[BinaryContent] 조회 성공: binaryContentId={}",
            result
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .get().id());

        return result;
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new FieldNotValidException("id");
        }

        log.trace("[BinaryContent] 첨부 파일 삭제 메서드 시작: id={}", id);

        BinaryContent binaryContent = getBinaryContent(id);

        log.info("[BinaryContent] 삭제 될 첨부 파일 정보: id={}", binaryContent.getId());

        binaryContentRepository.deleteById(id);

        log.info("[BinaryContent] 첨부 파일 삭제 성공: id={}", id);
    }

    public BinaryContent getBinaryContent(UUID id) {
        return binaryContentRepository.findById(id)
            .orElseThrow(() -> new BinaryContentNotFoundException(id));
    }

}
