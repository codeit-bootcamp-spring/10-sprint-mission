package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> postMessage(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                  @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
                                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments){
        log.debug("메시지 생성 요청: authorId={}, channelId={}, attachmentCount={}",
                request.getAuthorId(),
                request.getChannelId(),
                attachments == null ? 0 : attachments.size());

        MessageDto response = messageService.create(request, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 채널 내 메시지 조회(키워드 따라 조회 가능)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam(required = true) UUID channelId,
                                                                    @RequestParam(required = false) Instant cursor,
                                                                    @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(messageService.findAllMessagesByChannelId(channelId, cursor, pageable));
    }

    // 메시지 업데이트
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto> updateMessage(@PathVariable UUID id,
                                                    @Valid @RequestBody MessageUpdateRequest dto){
        log.debug("메시지 수정 요청: 메시지 id = {}", id);
        MessageDto response = messageService.update(id,dto);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id){
        log.debug("메시지 삭제 요청: 메시지 id = {}", id);
        messageService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }
}
