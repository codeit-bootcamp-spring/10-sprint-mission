package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelCreation;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelInfoUpdate;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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

    private List<UUID> getUserIdsInChannel(UUID channelId) {
        return readStatusRepository.findByChannelId(channelId)
                .stream()
                .map(ReadStatus::getUserId)
                .toList();
    }

    private ChannelResponse toPublicChannelResponse(Channel channel) {
        return ChannelResponse.builder()
                .channelId(channel.getId())
                .channelName(channel.getName())
                .description(channel.getDescription())
                .type(ChannelType.PUBLIC)
                .userIdsInChannel(null)
                .build();
    }

    private ChannelResponse toPrivateChannelResponse(Channel channel) {
        List<UUID> userIdsInChannel = getUserIdsInChannel(channel.getId());
        return ChannelResponse.builder()
                .channelId(channel.getId())
                .userIdsInChannel(userIdsInChannel)
                .type(ChannelType.PRIVATE).build();
    }

    private ChannelResponse toResponse(Channel channel) {
        if (channel.getType() == ChannelType.PUBLIC) {
            return toPublicChannelResponse(channel);
        }
        return toPrivateChannelResponse(channel);
    }

    private ChannelResponse createPrivateChannel(List<UUID> userIdsInChannel) {
        Channel channel = new Channel(ChannelType.PRIVATE, "private channel", "no content");
        channelRepository.save(channel);
        userIdsInChannel.stream()
                .map(userId -> new ReadStatus(userId, channel.getId()))
                .forEach(readStatusRepository::save);
        return toResponse(channel);
    }

    private ChannelResponse createPublicChannel(String channelName, String description) {
        Channel channel = new Channel(ChannelType.PUBLIC, channelName, description);
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public ChannelResponse create(ChannelCreation model) {
        if (model.type() == ChannelType.PUBLIC) {
            return createPublicChannel(model.channelName(), model.description());
        }
        return createPrivateChannel(model.userIdsInChannel());
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted(channelId)));
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAll() {
        return channelRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private boolean hasPrivateChannelMember(UUID channelId, UUID userId) {
        ChannelResponse response = find(channelId);
        if (response.type() == ChannelType.PUBLIC) {
            return false;
        }
        return response.userIdsInChannel().contains(userId);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll()
                .stream()
                .filter(channel -> (channel.getType() == ChannelType.PUBLIC)
                        || hasPrivateChannelMember(channel.getId(), userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelInfoUpdate model) {
        Channel channel = channelRepository.findById(model.channelId())
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted(model.channelId())));
        if (channel.getType() == ChannelType.PRIVATE) {
            // private channel can't be modified
            return toResponse(channel);
        }
        channel.update(model.newName(), model.newDescription());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(channelId));
        }
        messageRepository.findAll()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageRepository.deleteById(message.getId()));
        readStatusRepository.findByChannelId(channelId)
                .forEach(status -> readStatusRepository.deleteById(status.getId()));
        channelRepository.deleteById(channelId);
    }
}
