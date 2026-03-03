package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelUpdateCommand;
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
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService extends BasicDomainService<Channel> implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final IdGenerator idGenerator;

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        Channel channel = new Channel(idGenerator.generateId(), request.name(), request.description());
        channelRepository.save(channel);
        return channel.toResponse();
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(idGenerator.generateId(), request.participantIds());
        channelRepository.save(channel);
        request.participantIds()
                .stream()
                .map(userId -> new ReadStatus(userId, channel.getId()))
                .forEach(readStatusRepository::save);
        return channel.toResponse();
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        return findById(channelId).toResponse();
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.filter(channel -> channel.isVisibleTo(userId))
                .map(Channel::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(PublicChannelUpdateCommand command) {
        Channel channel = findById(command.id());
//        MessageResponse lastMsgResp = getLastMessageResponse(channel.getId());
        if (channel.matchChannelType(ChannelType.PRIVATE)) {
            throw new APIException(ErrorCode.PRIVATE_CHANNEL_NOT_UPDATE, command.id());
        }
        channel.update(command.name(), command.description());
        channelRepository.save(channel);
        return channel.toResponse();
    }

    @Override
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new APIException(ErrorCode.CHANNELID_NOT_FOUND, id);
        }
        List<UUID> msgToDelete = messageRepository.filter(message -> message.isInChannel(id))
                .map(Message::getId)
                .toList();
        msgToDelete.forEach(messageRepository::deleteById);

        List<UUID> readStatusToDelete = readStatusRepository.filter(readStatus -> readStatus.matchChannelId(id))
                .map(ReadStatus::getId)
                .toList();
        readStatusToDelete.forEach(readStatusRepository::deleteById);

        channelRepository.deleteById(id);
    }

    @Override
    protected Channel findById(UUID id) {
        return findEntityById(id, channelRepository,
                () -> new APIException(ErrorCode.CHANNELID_NOT_FOUND, id));
    }

    // deprecated ?
    private MessageResponse getLastMessageResponse(UUID channelId) {
        return messageRepository.filter(message -> message.isInChannel(channelId))
                .max(Message::compareTo)
                .orElseThrow(() -> new NoSuchElementException("this channel have no message"))
                .toResponse();
    }
}
