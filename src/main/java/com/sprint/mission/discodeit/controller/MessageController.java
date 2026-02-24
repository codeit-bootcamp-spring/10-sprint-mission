package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성(보내기)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Message 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "{channelId | authorId}를 가진 채널 | 유저가 없습니다")
                    )
            )
    })
    public ResponseEntity<MessageResponseDTO> create(
            @Parameter(description = "Message 생성 정보")
            @RequestPart("messageCreateRequest") MessageCreateRequestDTO messageCreateRequestDTO,
            @Parameter(description = "Message 첨부 파일들")
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Optional<List<BinaryContentCreateRequestDTO>> attachmentsDTOList = toBinaryContentCreateRequestDTOList(attachments);
        MessageResponseDTO response = messageService.create(messageCreateRequestDTO, attachmentsDTOList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메시지 수정
    @PatchMapping(value = "/{messageId}")
    @Operation(summary = "Message 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정됨",
                    content = @Content(
                            schema = @Schema(implementation = MessageResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "messageId:{messageId}를 가진 메시지를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<MessageResponseDTO> update(
            @Parameter(description = "수정할 Message ID")
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequestDTO messageUpdateRequestDTO) {
        MessageResponseDTO response = messageService.update(messageId, messageUpdateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메시지 삭제
    @DeleteMapping(value = "/{messageId}")
    @Operation(summary = "Message 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "messageId:{messageId}를 가진 메시지를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID")
            @PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = MessageResponseDTO.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID")
            @RequestParam UUID channelId) {
        List<MessageResponseDTO> response = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메시지 생성시 첨부 파일을 Service에 전달 하기전 Optional<List<BinaryContentCreateRequestDTO>>로 변환하는 private 메서드
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
