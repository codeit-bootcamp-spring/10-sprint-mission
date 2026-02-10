package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateMessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UpdateMessageRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

//    @RequestMapping(method = RequestMethod.POST)
//    public MessageDto send(@Valid @RequestBody CreateMessageRequestDto request){
//        return messageService.create(request);
//    }

    @RequestMapping(method = RequestMethod.POST,
                    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public MessageDto send(@RequestPart("request") @Valid CreateMessageRequestDto request,
                           @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        //MultipartFile -> byte[] 변환
        List<BinaryContentDto> attachments = new ArrayList<>();
        if(files != null && !files.isEmpty()){
            for(MultipartFile file : files){
                attachments.add(new BinaryContentDto(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        file.getBytes()
                ));
            }
        }

        return messageService.create(request, attachments);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public MessageDto update(@PathVariable UUID messageId, @Valid @RequestBody UpdateMessageRequestDto request){
        return messageService.update(messageId, request);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    public void delete(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageDto> findAllByChannel(@RequestParam UUID channelId){
        return messageService.findAllByChannelId(channelId);
    }

}
