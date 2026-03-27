package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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
        // Id로 BinaryContent 조회 메서드 시작 로그
        log.trace("BinaryContent 조회 메서드 시작: id={}", id);

        // id null 체크
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");

        // BinaryContent 레포지토리에서 BinaryContent 조회
        BinaryContent binaryContent = binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 첨부파일을 찾지 못했습니다."));

        log.debug("조회된 BinaryContent 정보: id={}, fileName={}, size={}",
            binaryContent.getId(), binaryContent.getFileName(), binaryContent.getSize());

        log.info("BinaryContent 조회 성공");
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        log.trace("ID 리스트로 BinaryContent 조회 메서드 시작: ids={}", ids);

        // id 리스트 null 체크
        Objects.requireNonNull(ids, "유효하지 않은 식별자 목록!");

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
        log.trace("첨부 파일 삭제 메서드 시작: id={}", id);
        Objects.requireNonNull(id, "해당 ID는 유효하지 않습니다!");
        binaryContentRepository.deleteById(id);

        log.info("첨부 파일 삭제 성공");
    }

}
