package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(CreateBinaryContentRequestDTO dto) {
        validateCreateRequest(dto);
        CreateBinaryContentPayloadDTO payload
                = new CreateBinaryContentPayloadDTO(dto.data(), dto.contentType(), dto.filename(), dto.data().length);

        BinaryContent binaryContent = binaryContentMapper.toEntity(payload);
        binaryContentRepository.save(binaryContent);

        log.info("[BINARYCONTENT_CREATE_SUCCESS] 파일 생성 성공: binaryContentId={}", binaryContent.getId());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto findById(UUID binaryContentId) {
        BinaryContent binaryContent = findBinaryContentOrThrow(binaryContentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("ids", "ids is null")
            );
        }

        if (ids.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(ids);

        if (ids.size() != binaryContents.size()) {
            log.warn("[BINARYCONTENT_NOT_FOUND] 요청한 파일 id 중 일부가 존재하지 않음: binaryContentIdsSize={}", ids.size());
            throw new BinaryContentNotFoundException(
                    "요청한 BinaryContent id 중 일부가 존재하지 않습니다. 요청: " + ids.size()
                            + "건, 조회: " + binaryContents.size() + "건"
                    );
        }

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAll() {
        List<BinaryContent> binaryContents = binaryContentRepository.findAll();

        return binaryContentMapper.toDtoList(binaryContents);
    }

    @Override
    public void delete(UUID binaryContentId) {
        findBinaryContentOrThrow(binaryContentId);

        log.info("[BINARYCONTENT_DELETE_SUCCESS] 파일 삭제 성공: binaryContentId={}", binaryContentId);
        binaryContentRepository.deleteById(binaryContentId);
    }

    private BinaryContent findBinaryContentOrThrow(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("binaryContentId", "binaryContentId is null")
            );
        }

        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> {
                    log.warn("[BINARYCONTENT_NOT_FOUND] 파일이 존재하지 않음: binaryContentId={}", binaryContentId);
                    return new BinaryContentNotFoundException(binaryContentId);
                });

        return binaryContent;
    }

    private void validateCreateRequest(CreateBinaryContentRequestDTO dto) {
        if (dto == null) {
            throw new InvalidInputException(
                    ErrorCode.DTO_CAN_NOT_BE_NULL, Map.of("dto", "dto is null")
            );
        }

        if (dto.userId() == null) {
            log.warn("[BINARYCONTENT_CREATE_FAIL_BY_USERID] 유저 id가 null값으로 파일 생성 실패");
            throw new UserNotFoundException("userId는 null값일 수 없습니다.");
        }

        if (dto.data() == null || dto.data().length == 0) {
            log.warn("[BINARYCONTENT_CREATE_FAIL_BY_DATA] data값이 null/empty로 파일 생성 실패");
            throw new InvalidInputException(
                    ErrorCode.BINARY_CONTENT_DATA_IS_NULL, Map.of("data", "data값은 null/empty값일 수 없습니다.")
            );
        }

        if (dto.contentType() == null || dto.contentType().isBlank()) {
            log.warn("[BINARYCONTENT_CREATE_FAIL_BY_CONTENT_TYPE] contentType값이 null/empty로 파일 생성 실패");
            throw new InvalidInputException(
                    ErrorCode.BINARY_CONTENT_CONTENT_TYPE_IS_NULL, Map.of("contentType", "contentType값은 null/empty값일 수 없습니다.")
            );
        }

        if (dto.filename() == null || dto.filename().isBlank()) {
            log.warn("[BINARYCONTENT_CREATE_FAIL_BY_FILENAME] 파일명이 null/empty로 파일 생성 실패");
            throw new InvalidInputException(
                    ErrorCode.BINARY_CONTENT_FILENAME_IS_NULL, Map.of("filename", "filename값은 null/empty값일 수 없습니다.")
            );
        }
    }
}
