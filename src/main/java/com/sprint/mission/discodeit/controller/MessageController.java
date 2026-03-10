package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;
  private final UserService userService;
  private final PageResponseMapper pageResponseMapper;

  public MessageController(
      MessageService messageService,
      BinaryContentService binaryContentService,
      UserService userService,
      PageResponseMapper pageResponseMapper
  ) {
    this.messageService = messageService;
    this.binaryContentService = binaryContentService;
    this.userService = userService;
    this.pageResponseMapper = pageResponseMapper;
  }

  @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId", tags = {
      "Message"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      Pageable pageable
  ) {
    Slice<MessageResponse> slice = messageService.findAllByChannelId(channelId, pageable);

    Slice<MessageDto> dtoSlice = slice.map(this::toDto);

    return ResponseEntity.ok(pageResponseMapper.fromSlice(dtoSlice));
  }

  @Operation(summary = "Message 생성", operationId = "create_2", tags = {"Message"})
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음")
  })
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<UUID> attachmentIds = uploadAttachments(attachments);

    MessageCreateRequest newRequest = new MessageCreateRequest(
        request.channelId(),
        request.authorId(),
        request.content(),
        attachmentIds
    );

    MessageResponse created = messageService.create(newRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
  }

  @Operation(summary = "Message 내용 수정", operationId = "update_2", tags = {"Message"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest body
  ) {
    MessageUpdateRequest req = new MessageUpdateRequest(messageId, body.newContent());
    return ResponseEntity.ok(toDto(messageService.update(req)));
  }

  @Operation(summary = "Message 삭제", operationId = "delete_1", tags = {"Message"})
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private MessageDto toDto(MessageResponse response) {
    var authorResponse = userService.find(response.userId());
    var author = toUserDto(authorResponse);

    List<BinaryContentDto> attachments = response.attachmentIds() == null
        ? List.of()
        : binaryContentService.findAllByIdIn(response.attachmentIds()).stream()
            .map(this::toBinaryDto)
            .toList();

    return new MessageDto(
        response.messageId(),
        response.createdAt(),
        response.updatedAt(),
        response.content(),
        response.channelId(),
        author,
        attachments
    );
  }

  private com.sprint.mission.discodeit.dto.user.UserDto toUserDto(
      com.sprint.mission.discodeit.dto.user.UserResponse userResponse
  ) {
    BinaryContentDto profile = null;
    if (userResponse.profileImageId() != null) {
      BinaryContentResponse binary = binaryContentService.find(userResponse.profileImageId());
      profile = toBinaryDto(binary);
    }

    return new com.sprint.mission.discodeit.dto.user.UserDto(
        userResponse.id(),
        userResponse.userName(),
        userResponse.email(),
        profile,
        userResponse.online()
    );
  }

  private BinaryContentDto toBinaryDto(BinaryContentResponse response) {
    return new BinaryContentDto(
        response.id(),
        response.fileName(),
        response.size(),
        response.contentType()
    );
  }

  private List<UUID> uploadAttachments(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }

    List<UUID> ids = new ArrayList<>();
    for (MultipartFile file : attachments) {
      if (file == null || file.isEmpty()) {
        continue;
      }

      try {
        UUID id = binaryContentService.create(
            new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                null,
                null
            )
        );
        ids.add(id);
      } catch (IOException e) {
        throw new BusinessLogicException(ErrorCode.FILE_IO_ERROR);
      }
    }
    return List.copyOf(ids);
  }
}