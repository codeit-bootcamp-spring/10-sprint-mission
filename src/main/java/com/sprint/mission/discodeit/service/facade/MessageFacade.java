package com.sprint.mission.discodeit.service.facade;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 웹 계층(Controller)과 도메인 계층(Service) 사이에서
 * 첨부 파일 처리와 메시지 생성 트랜잭션을 하나로 묶어주는 퍼사드 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class MessageFacade {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @Transactional
  public MessageDto.Response createMessage(MessageDto.CreateRequest request, List<MultipartFile> files) {
    List<UUID> attachmentIds = uploadMessageFiles(files);
    return messageService.create(request, attachmentIds);
  }

  private List<UUID> uploadMessageFiles(List<MultipartFile> files) {
    // 리스트가 null이거나 비어있으면 빈 리스트 반환
    if (files == null || files.isEmpty()) {
      return List.of();
    }

    return files.stream()
        .filter(file -> file != null && !file.isEmpty()) // 리스트 내부의 null 파일도 안전하게 필터링
        .map(file -> binaryContentService.create(binaryContentService.multipartFileToCreateRequest(file)).id())
        .toList();
  }
}
