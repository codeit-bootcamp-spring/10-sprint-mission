package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성(보내기)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> create(@RequestPart("messageCreateRequestDTO") MessageCreateRequestDTO messageCreateRequestDTO,
                                                     @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Optional<List<BinaryContentCreateRequestDTO>> attachmentsDTOList = toBinaryContentCreateRequestDTOList(attachments);
        MessageResponseDTO response = messageService.create(messageCreateRequestDTO, attachmentsDTOList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메시지 수정
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> update(@PathVariable("message-id") UUID messageId,
                                                     @RequestPart("messageUpdateRequestDTO") MessageUpdateRequestDTO messageUpdateRequestDTO,
                                                     @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Optional<List<BinaryContentCreateRequestDTO>> attachmentsDTOList = toBinaryContentCreateRequestDTOList(attachments);
        MessageResponseDTO response = messageService.update(messageId, messageUpdateRequestDTO, attachmentsDTOList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메시지 삭제
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 채널 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId) {
        List<MessageResponseDTO> response = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메시지 생성, 수정 시 첨부 파일을 Service에 전달 하기전 Optional<List<BinaryContentCreateRequestDTO>>로 변환하는 private 메서드
    private Optional<List<BinaryContentCreateRequestDTO>> toBinaryContentCreateRequestDTOList(List<MultipartFile> attachments) {
        return Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequestDTO(
                                        file.getOriginalFilename(),
                                        file.getBytes(),
                                        file.getContentType()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList()
                );
    }
}
