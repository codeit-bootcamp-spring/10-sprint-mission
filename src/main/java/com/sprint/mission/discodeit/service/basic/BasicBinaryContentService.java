package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto create(BinaryContentCreateRequest binaryContentCreateRequest) {
        String fileName = binaryContentCreateRequest.fileName();
        byte[] bytes = binaryContentCreateRequest.bytes();
        String contentType = binaryContentCreateRequest.contentType();
        BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length, bytes, contentType);
        return binaryContentMapper.toDto(binaryContentRepository.save(binaryContent));
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID binaryContentId) {
        BinaryContent binaryContent = getBinaryContentByIdOrThrow(binaryContentId);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            throw new IllegalArgumentException("입력된 binaryContentIds가 null 또는 빈 리스트 입니다");
        }
        List<BinaryContentDto> binaryContentDtoList = new ArrayList<>();
        for (UUID binaryContentId : binaryContentIds) {
            BinaryContent binaryContent = getBinaryContentByIdOrThrow(binaryContentId);
            binaryContentDtoList.add(binaryContentMapper.toDto(binaryContent));
        }
        return binaryContentDtoList;
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(binaryContentId+"를 가진 BinaryContent를 찾지 못했습니다");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

    // BinaryContentRepository.findById()를 통한 반복되는 BinaryContent 조회/예외처리를 중복제거 하기 위한 메서드
    private BinaryContent getBinaryContentByIdOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("binaryContentId:"+binaryContentId+"를 가진 BinaryContent를 찾지 못했습니다"));
    }
}
