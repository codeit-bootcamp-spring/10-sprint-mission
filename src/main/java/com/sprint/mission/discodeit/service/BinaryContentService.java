package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateInfo;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentInfo;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BinaryContentService {
    private final BinaryContentRepository contentRepository;

    public BinaryContentInfo createBinaryContent(BinaryContentCreateInfo contentInfo) {
        BinaryContent content = new BinaryContent(contentInfo.content());
        contentRepository.save(content);
        return BinaryContentMapper.toBinaryContentInfo(content);
    }

    public BinaryContentInfo findBinaryContent(UUID contentId) {
        BinaryContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NoSuchElementException("해당 콘텐츠가 존재하지 않습니다."));
        return BinaryContentMapper.toBinaryContentInfo(content);
    }

    public List<BinaryContentInfo> findAll() {
        return contentRepository.findAll()
                .stream()
                .map(BinaryContentMapper::toBinaryContentInfo)
                .toList();
    }

    public List<BinaryContentInfo> findAllByIdIn(List<UUID> contentIds) {
        return contentRepository.findAll()
                .stream()
                .filter(content -> contentIds.contains(content.getId()))
                .map(BinaryContentMapper::toBinaryContentInfo)
                .toList();
    }

    public void deleteBinaryContent(UUID contentId) {
        contentRepository.deleteById(contentId);
    }
}
