package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    /*
     **메시지 관리**
    - [ ]  메시지를 보낼 수 있다.
    - [ ]  메시지를 수정할 수 있다.
    - [ ]  메시지를 삭제할 수 있다.
    - [ ]  특정 채널의 메시지 목록을 조회할 수 있다.
     */
    private final MessageService messageService;
    private final BinaryContentMapper binaryContentMapper;

    @RequestMapping(path = "/channels/{channelid}/messages", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MessageResponseDto> sendMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID channelid,
            @RequestPart(value = "messageCreateDto",required = false) MessageCreateDto dto,
            @RequestPart(value = "attachment",required = false) List<MultipartFile> multipartFiles)
    {
        List<BinaryContentCreateDto> binaryContentCreateDtos = new ArrayList<>();
        if(multipartFiles != null){
            for (MultipartFile multipartFile : multipartFiles) {
                binaryContentCreateDtos.add(binaryContentMapper.toCreateDto(multipartFile));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(channelid,userId,dto,binaryContentCreateDtos));
    }

    @RequestMapping(path = "/messages/{messageid}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> updateMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID messageid,
            @RequestBody MessageUpdateDto dto)
    {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(messageService.update(messageid,userId,dto));
    }

    @RequestMapping(path = "/messages/{messageid}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID messageid
            )
    {
        messageService.delete(messageid,userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/channels/{channelid}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> findMessagesByChannelId(
            @RequestHeader UUID userId,
            @PathVariable UUID channelid)
    {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findAllByChannelId(userId,channelid));
    }

}
