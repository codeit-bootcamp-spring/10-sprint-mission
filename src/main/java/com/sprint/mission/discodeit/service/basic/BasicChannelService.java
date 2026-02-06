package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.*;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final String ID_NOT_FOUND = "Channel with id %s not found";
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private ChannelResponse createPrivateChannel(PrivateChannelCreation model) {
        Channel channel = new Channel(model.userIdsInPrivateChannel());
        channelRepository.save(channel);
        ChannelResponse response = channel.toResponse();
        response.userIdsInPrivateChannel().stream()
                .map(userId -> new ReadStatus(userId, response.channelId()))
                .forEach(readStatusRepository::save);
        return response;
    }

    private ChannelResponse createPublicChannel(PublicChannelCreation model) {
        Channel channel = new Channel(model.channelName(), model.description());
        channelRepository.save(channel);
        return channel.toResponse();
    }

    @Override
    public ChannelResponse create(ChannelCreation model) {
        if (model.type() == ChannelType.PUBLIC) {
            return createPublicChannel(
                    new PublicChannelCreation(model.channelName(), model.description()));
        }
        return createPrivateChannel(
                new PrivateChannelCreation(model.userIdsInChannel()));
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted(channelId)));
        MessageResponse lastMessageResponse = getLastMessageResponse(channelId);
        return channel.toResponse(lastMessageResponse.createdAt());
    }

    private MessageResponse getLastMessageResponse(UUID channelId) {
        return messageRepository.findAll()
                .stream()
                .filter(message -> message.isInChannel(channelId))
                .max(Message::compareTo)
                .orElseThrow()
                .toResponse();
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll()
                .stream()
                .filter(channel -> isVisibleTo(channel, userId))
                .map(channel -> {
                    MessageResponse lastMessageResponse = getLastMessageResponse(channel.getId());
                    return channel.toResponse(lastMessageResponse.createdAt());
                })
                .toList();
    }

    private boolean isVisibleTo(Channel channel, UUID userId) {
        return (channel.matchChannelType(ChannelType.PUBLIC))
                || (channel.isPrivateMember(userId));
    }

    @Override
    public ChannelResponse update(PublicChannelUpdate model) {
        // todo refactoring
        Channel channel = channelRepository.findById(model.channelId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted(model.channelId())));
        MessageResponse lastMsgResp = getLastMessageResponse(channel.getId());
        if (channel.matchChannelType(ChannelType.PRIVATE)) {
            // private channel can't be modified
            return channel.toResponse(lastMsgResp.createdAt());
        }
        channel.update(model.newName(), model.newDescription());
        channelRepository.save(channel);
        return channel.toResponse(lastMsgResp.createdAt());
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(channelId));
        }
        // todo: move to repo
        List<MessageResponse> msgToDelete = messageRepository.findAll()
                .stream()
                .filter(message -> message.isInChannel(channelId))
                .map(Message::toResponse)
                .toList();
        msgToDelete.forEach(msg -> messageRepository.deleteById(msg.id()));

        // todo: move to repo
        readStatusRepository.findByChannelId(channelId)
                .forEach(status -> readStatusRepository.deleteById(status.getId()));

        channelRepository.deleteById(channelId);
    }
}
