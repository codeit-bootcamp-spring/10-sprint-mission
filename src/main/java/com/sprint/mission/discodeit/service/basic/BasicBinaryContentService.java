package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper mapper;
    private final MessageRepository messageRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request){
        request.validate();
        BinaryContent bc = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        binaryContentRepository.save(bc);
        return mapper.toResponse(bc);
    }

    @Override
    public BinaryContentResponse findById(UUID messageId) {
        if (messageId == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        BinaryContent bc = binaryContentRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent가 없습니다."));

        return mapper.toResponse(bc);
    }


    @Override
    public List<BinaryContentResponse> findAll(UUID messageId) {
        if (messageId == null) throw new IllegalArgumentException("ids는 null일 수 없습니다.");

        // 1) 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지가 없습니다."));

        // 2) 메시지가 가진 첨부파일 id 목록
        List<UUID> attachmentIds = message.getAttachmentIds(); // 프로젝트에 맞게 메서드명 조정

        // 첨부파일이 없으면 빈 리스트 반환
        if (attachmentIds == null || attachmentIds.isEmpty()) return List.of();

        // 3) 첨부파일들 조회 → Response로 변환
        return attachmentIds.stream()
                .map(this::findById) // findById가 BinaryContentResponse를 반환하는 메서드라고 가정
                .toList();
    }


    @Override
    public void delete(UUID id) {
        if (id==null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        binaryContentRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("삭제할 BinaryContent가 없습니다."));
        binaryContentRepository.delete(id);
    }



}
