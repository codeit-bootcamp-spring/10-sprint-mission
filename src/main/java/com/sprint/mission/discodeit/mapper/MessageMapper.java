package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public MessageResponse toResponse(Message message) {
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() ->
                        new NoSuchElementException("보낸 유저가 없습니다: " + message.getSenderId()));

        Channel channel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() ->
                        new NoSuchElementException("채널이 없습니다: " + message.getChannelId()));

        return new MessageResponse(
                message.getId(),
                message.getMessageContent(),
                message.getCreatedAt(),

                sender.getId(),
                sender.getUserName(),
                sender.getAlias(),

                channel.getId(),
                channel.getChannelName(),

                message.getAttachmentIds()
        );
    }
}
