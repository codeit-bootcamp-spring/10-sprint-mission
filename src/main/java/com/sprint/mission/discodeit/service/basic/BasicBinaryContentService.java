package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.FileProcessingException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 바이너리 컨텐츠(이미지, 파일 등)의 메타데이터 관리 및 업로드 이벤트를 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * MultipartFile을 생성 요청 DTO로 변환합니다.
     */
    @Override
    public BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentDto.CreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            log.error("[BinaryContent] 파일 읽기 실패: FileName={}", file.getOriginalFilename(), e);
            throw FileProcessingException.readFailed(file.getOriginalFilename(), e);
        }
    }

    /**
     * 신규 바이너리 컨텐츠 메타데이터를 저장하고 업로드 프로세스를 시작(이벤트 발행)합니다.
     *
     * @param request 생성 요청 정보
     * @return 저장된 컨텐츠 상세 정보
     */
    @Override
    @Transactional
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes().length,
                BinaryContentStatus.PROCESSING
        );
        
        BinaryContent savedContent = binaryContentRepository.save(binaryContent);
        log.info("[BinaryContent] 메타데이터 저장 완료 (PROCESSING): ID={}, Name={}", savedContent.getId(), savedContent.getFileName());

        // 실제 파일 저장을 위한 이벤트 발행 (S3/로컬 저장소 등에서 비동기 처리)
        eventPublisher.publishEvent(new BinaryContentEvents.Created(savedContent.getId(), request.bytes()));

        return binaryContentMapper.toResponse(savedContent);
    }

    /**
     * 바이너리 컨텐츠를 ID로 조회합니다.
     */
    @Override
    public BinaryContentDto.Response find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    }

    /**
     * 여러 ID의 컨텐츠를 한 번에 조회하고 모든 존재 여부를 검증합니다.
     */
    @Override
    public List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds) {
        Set<UUID> uniqueIds = new HashSet<>(binaryContentIds);
        List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(uniqueIds);

        validateAllContentsFound(uniqueIds, contents);

        return contents.stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    /**
     * 컨텐츠 메타데이터를 삭제합니다.
     */
    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        binaryContentRepository.delete(binaryContent);
        log.info("[BinaryContent] 메타데이터 삭제 완료: ID={}", binaryContentId);
    }

    /**
     * 컨텐츠의 처리 상태를 업데이트합니다. (업로드 완료/실패 등)
     */
    @Override
    @Transactional
    public BinaryContentDto.Response updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        binaryContent.updateStatus(status);
        log.info("[BinaryContent] 상태 변경: ID={}, Status={}", binaryContentId, status);
        
        return binaryContentMapper.toResponse(binaryContent);
    }

    // --- Private Helpers ---

    private void validateAllContentsFound(Set<UUID> requestIds, List<BinaryContent> foundContents) {
        if (foundContents.size() != requestIds.size()) {
            Set<UUID> foundIds = foundContents.stream().map(BinaryContent::getId).collect(Collectors.toSet());
            List<UUID> missingIds = requestIds.stream().filter(id -> !foundIds.contains(id)).toList();
            log.warn("[BinaryContent] 일부 컨텐츠를 찾을 수 없음: MissingCount={}, MissingIds={}", missingIds.size(), missingIds);
            throw BinaryContentNotFoundException.withIds(requestIds.size(), missingIds);
        }
    }
}
