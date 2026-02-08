package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelOwnerChangeRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateInput;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseWithLastMessageTime;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 채널 관리 Controller
 * 추후, 쿠키/세션 배우고 userId를 빼서 전달하게 수정?
 */
@RestController
@RequestMapping("/channels")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    /**
     * 공개 채널 생성
     */
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity createPublicChannel(@RequestBody @Valid PublicChannelCreateRequest request) {
        Channel channel = channelService.createPublicChannel(request);

        ChannelResponse result = createChannelResponse(channel);

        return ResponseEntity.status(201).body(result);
    }

    /**
     * 비공개 채널 생성
     */
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity createPrivateChannel(@RequestBody @Valid PrivateChannelCreateRequest request) {
        Channel channel = channelService.createPrivateChannel(request);

        ChannelResponse result = createChannelResponse(channel);

        return ResponseEntity.status(201).body(result);
    }

    /**
     * 특정 사용자가 볼 수 있는 모든 채널 목록
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity findAllChannelsByUserId(@PathVariable UUID userId) {
        List<ChannelResponseWithLastMessageTime> result = channelService.findAllByUserId(userId);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 공개 채널의 정보 수정
     */
    @RequestMapping(value = "/public/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity updatePublicChannelInfo(@PathVariable UUID channelId,
                                                  @RequestBody @Valid ChannelUpdateRequest request) {
        ChannelUpdateInput input = new ChannelUpdateInput(channelId, request.ownerId(),
                request.channelName(), request.channelDescription());
        Channel channel = channelService.updateChannelInfo(input);

        ChannelResponse result = createChannelResponse(channel);
        return ResponseEntity.status(200).body(result);
    }

    /**
     * 채널 참여
     */
    @RequestMapping(value = "/{channelId}/join", method = RequestMethod.POST)
    public ResponseEntity joinChannel(@PathVariable UUID channelId, @RequestBody UUID userId) {
        Channel channel = channelService.joinChannel(userId, channelId);
        ChannelResponse result = createChannelResponse(channel);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 채널 탈퇴
     */
    @RequestMapping(value = "/{channelId}/leave", method = RequestMethod.DELETE)
    public ResponseEntity leaveChannel(@PathVariable UUID channelId, @RequestBody UUID userId) {
        Channel channel = channelService.leaveChannel(userId, channelId);
        ChannelResponse result = createChannelResponse(channel);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 채널 owner 변경
     */
    @RequestMapping(value = "/{channelId}/change-owner", method = RequestMethod.PATCH)
    public ResponseEntity changeChannelOwner(@PathVariable UUID channelId,
                                             @RequestBody ChannelOwnerChangeRequest request) {
        Channel channel = channelService.changeChannelOwner(request.currentUserId(), channelId, request.newOwnerId());
        ChannelResponse result = createChannelResponse(channel);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 채널 삭제
     */
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteChannel(@PathVariable UUID channelId,
                                        @RequestBody UUID userId) {
        channelService.deleteChannel(userId, channelId);
        return ResponseEntity.status(204).build();
    }

    private ChannelResponse createChannelResponse(Channel channel) {
        List<UUID> channelMembersIds = channel.getChannelMembersList().stream().map(member -> member.getId()).toList();
        return new ChannelResponse(channel.getId(), channel.getOwner().getId(),
                channel.getChannelType(), channel.getChannelName(), channel.getChannelDescription(), channelMembersIds);
    }

    // exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity handleException(IllegalStateException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException e) {
        String ErrorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
