package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UUID createPublicChannel(CreatePublicChannelRequest request) {
        Channel channel = Channel.buildPublic(
                request.name(),
                request.description()
        );

        channelRepository.save(channel);
        return channel.getId();
    }

    @Override
    public UUID createPrivateChannel(CreatePrivateChannelRequest request) {
        Channel channel = Channel.buildPrivate(
                request.memberIds()
        );

        for (UUID memberId : request.memberIds()) {
            validateMemberExists(memberId);

            ReadStatus readStatus = new ReadStatus(
                    memberId,
                    channel.getId()
            );

            readStatusRepository.save(readStatus);
        }

        channelRepository.save(channel);
        return channel.getId();
    }

    private void validateMemberExists(UUID memberId) {
        if (!userRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 memberId 입니다. memberId: " + memberId);
        }
    }

    //TODO: ReadStatus update???
    @Override
    public ChannelResponse findChannelByChannelId(UUID id) {
        Channel channel = getChannelOrThrow(id);

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

        return ChannelResponse.of(channel, lastMessageAt);
    }

    public Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }

    @Override
    public List<ChannelResponse> findAllChannelsByUserId(UUID requesterId) {
        List<Channel> channels = channelRepository.findAll();

        List<Channel> visibleChannels = channels.stream()
                .filter(channel ->
                        channel.isPublic() ||
                                (channel.isPrivate() && channel.hasMember(requesterId))
                )
                .toList();

        List<UUID> channelIds = visibleChannels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, Instant> lastMessageMap =
                messageRepository.findLastMessageAtByChannelIds(channelIds);

        return channels.stream()
                .map(channel -> {
                    Instant lastMessageAt = lastMessageMap.get(channel.getId());

                    return ChannelResponse.of(channel, lastMessageAt);
                })
                .toList();
    }

    @Override
    public ChannelResponse updateChannelInfo(UpdateChannelRequest request) {
        Channel channel = getChannelOrThrow(request.channelId());

        validateChannelType(channel);
        channel.updateInfo(
                request.name(),
                request.description()
        );

        channelRepository.save(channel);

        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());
        return ChannelResponse.of(channel, lastMessageAt);
    }

    private static void validateChannelType(Channel channel) {
        if (channel.isPrivate()) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다. channelId: " + channel.getId());
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        for (UUID messageId : new ArrayList<>(channel.getMessageIds())) {
            channel.removeMessage(messageId);
            messageRepository.deleteById(messageId);
        }

        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channel.getId());

        for (ReadStatus readStatus : new ArrayList<>(readStatuses)) {
            readStatusRepository.delete(readStatus);
        }

        channelRepository.save(channel);
        channelRepository.delete(channel);
    }
}
