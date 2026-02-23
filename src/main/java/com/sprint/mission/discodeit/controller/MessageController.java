package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "201",
            description = "Message가 성공적으로 생성됨",
            content = @Content(
                schema = @Schema(implementation = MessageResponseDTO.class)
            )
        )
    })
    public MessageResponseDTO sendMessage(@RequestParam List<byte[]> attachments,
        @RequestBody MessageCreateRequestDTO req) {
        return messageService.create(attachments, req);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Message가 성공적으로 수정됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Message with id {messageId} not found")
            )
        )
    })
    public MessageResponseDTO editMessage(@RequestParam(name = "messageId") UUID messageId,
        @RequestBody MessageUpdateRequestDto req) {
        return messageService.update(messageId, req);
    }


    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Message가 성공적으로 삭제됨"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Message with id {messageId} not found")
            )
        )
    })
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiResponse(
        responseCode = "200",
        description = "Message 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = MessageResponseDTO.class))
        )
    )
    public List<MessageResponseDTO> viewChannelMessage(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);


    }
}
