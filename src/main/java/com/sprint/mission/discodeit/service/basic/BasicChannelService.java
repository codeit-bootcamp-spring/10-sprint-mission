package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
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
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = Channel.buildPublic(
                request.name(),
                request.description()
        );

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = Channel.buildPrivate(
                request.participantIds()
        );

//        for (UUID memberId : request.participantIds()) {
//            validateMemberExists(memberId);
//
//            ReadStatus readStatus = new ReadStatus(
//                    memberId,
//                    channel.getId(),
//                    null
//            );
//
//            readStatusRepository.save(readStatus);
//        }

        channelRepository.save(channel);
        return channel;
    }

//    private void validateMemberExists(UUID memberId) {
//        if (!userRepository.existsById(memberId)) {
//            throw new ApiException(ErrorCode.USER_NOT_FOUND,
//                    "존재하지 않는 memberId 입니다. memberId: " + memberId);
//        }
//    }

    @Override
    public Channel findChannelByChannelId(UUID channelId) {
        return getChannelOrThrow(channelId);
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + id));
    }

    @Override
    public List<ChannelDto> findAllChannelsByUserId(UUID requesterId) {
        List<Channel> channels = channelRepository.findVisibleChannels(requesterId);

        List<UUID> channelIds = channels.stream()
                .map(Channel::getId)
                .toList();

        Map<UUID, Instant> lastMessageMap =
                messageRepository.findLastMessageAtByChannelIds(channelIds);

        return channels.stream()
                .map(channel -> {
                    Instant lastMessageAt = lastMessageMap.get(channel.getId());

                    return ChannelDto.of(channel, lastMessageAt);
                })
                .toList();
    }

    @Override
    public Channel updateChannelInfo(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(channelId);
        validateChannelType(channel);

        channel.updateInfo(
                request.newName(),
                request.newDescription()
        );

        channelRepository.save(channel);
        return channel;
    }

    private void validateChannelType(Channel channel) {
        if (channel.isPrivate()) {
            throw new ApiException(ErrorCode.CHANNEL_UPDATE_FORBIDDEN,
                    "PRIVATE 채널은 수정할 수 없습니다. channelId: " + channel.getId());
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        for (UUID messageId : new ArrayList<>(channel.getMessageIds())) {
            channel.removeMessage(messageId);
            messageRepository.deleteById(messageId);
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

        for (ReadStatus readStatus : new ArrayList<>(readStatuses)) {
            readStatusRepository.delete(readStatus);
        }

        channelRepository.save(channel);
        channelRepository.delete(channel);
    }
}
