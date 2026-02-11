package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateInfo;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentInfo;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentsRequest;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .orElseThrow(BinaryContentNotFoundException::new);
        return BinaryContentMapper.toBinaryContentInfo(content);
    }

    public BinaryContent findBinaryContentEntity(UUID contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(BinaryContentNotFoundException::new);
    }

    public List<BinaryContentInfo> findAll() {
        return contentRepository.findAll()
                .stream()
                .map(BinaryContentMapper::toBinaryContentInfo)
                .toList();
    }

    public List<BinaryContentInfo> findAllByIdIn(BinaryContentsRequest request) {
        return contentRepository.findAll()
                .stream()
                .filter(content -> request.contentIds().contains(content.getId()))
                .map(BinaryContentMapper::toBinaryContentInfo)
                .toList();
    }

    public void deleteBinaryContent(UUID contentId) {
        contentRepository.deleteById(contentId);
    }
}
