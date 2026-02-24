package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  //POST /api/messages (multipart/form-data)
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryRequests = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile file : attachments) {
        try {
          binaryRequests.add(new BinaryContentCreateRequest(
              file.getOriginalFilename(),
              file.getContentType(),
              file.getBytes()
          ));
        } catch (IOException e) {
          throw new RuntimeException("파일 읽기 실패:" + file.getOriginalFilename(), e);
        }
      }
    }
    Message created = messageService.create(messageCreateRequest, binaryRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //GET /api/messages?channelId={}
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam UUID channelId
  ) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }

  //PATCH /api/messages/{messageId}
  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updated = messageService.update(messageId, request);
    return ResponseEntity.ok(updated);
  }

  //DELETE /api/messages/{messageId}
  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }
}

//
//  @RequestMapping(
//      path = "create",
//      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//  )
//  public ResponseEntity<Message> create(
//      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
//      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
//  ) {
//    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
//        .map(files -> files.stream()
//            .map(file -> {
//              try {
//                return new BinaryContentCreateRequest(
//                    file.getOriginalFilename(),
//                    file.getContentType(),
//                    file.getBytes()
//                );
//              } catch (IOException e) {
//                throw new RuntimeException(e);
//              }
//            })
//            .toList())
//        .orElse(new ArrayList<>());
//    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
//    return ResponseEntity
//        .status(HttpStatus.CREATED)
//        .body(createdMessage);
//  }
//
//  @RequestMapping(path = "update")
//  public ResponseEntity<Message> update(@RequestParam("messageId") UUID messageId,
//      @RequestBody MessageUpdateRequest request) {
//    Message updatedMessage = messageService.update(messageId, request);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(updatedMessage);
//  }
//
//  @RequestMapping(path = "delete")
//  public ResponseEntity<Void> delete(@RequestParam("messageId") UUID messageId) {
//    messageService.delete(messageId);
//    return ResponseEntity
//        .status(HttpStatus.NO_CONTENT)
//        .build();
//  }
//
//  @RequestMapping("findAllByChannelId")
//  public ResponseEntity<List<Message>> findAllByChannelId(
//      @RequestParam("channelId") UUID channelId) {
//    List<Message> messages = messageService.findAllByChannelId(channelId);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(messages);
//  }

