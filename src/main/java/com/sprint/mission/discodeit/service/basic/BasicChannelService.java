package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelRequestUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponseDto createPublic(PublicChannelRequestCreateDto request) {
        Validators.validateCreatePublicChannel(request.channelName(), request.channelDescription());
        Channel channel = new Channel(ChannelType.PUBLIC, request.channelName(), request.channelDescription());

        Channel savedChannel = channelRepository.save(channel);
        return toDto(savedChannel, null);
    }

    @Override
    public ChannelResponseDto createPrivate(PrivateChannelRequestCreateDto request) {
        Validators.validateCreatePrivateChannel(request.joinedUserIds());
        List<User> users = request.joinedUserIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다: " + id)))
                .toList();



        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        channel.getJoinedUserIds().addAll(
                users.stream()
                        .map(User :: getId)
                        .toList()
        );

        Channel savedChannel = channelRepository.save(channel);

        List<ReadStatus> readStatuses = users.stream()
                .map(user -> {
                   return new ReadStatus(user.getId(), savedChannel.getId());
                })
                .toList();

        readStatusRepository.saveAll(readStatuses);

        return toDto(savedChannel, null);
    }


    @Override
    public ChannelResponseDto find(UUID id) {
        Channel channel = validateExistenceChannel(id);
        Instant lastMessageAt = getLastMessageAt(id);

        return toDto(channel, lastMessageAt);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID id) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if(channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }
                    return channel.getJoinedUserIds().contains(id);
                })
                .map(channel -> toDto(channel, getLastMessageAt(id)))
                .toList();
    }

    @Override
    public ChannelResponseDto updateChannel(ChannelRequestUpdateDto request) {
        Validators.requireNonNull(request, "request");
        Channel channel = validateExistenceChannel(request.id());

        if(channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        Optional.ofNullable(request.channelName())
                .ifPresent(name -> {Validators.requireNotBlank(name, "channelName");
                    channel.updateChannelName(name);
                });
        Optional.ofNullable(request.channelDescription()).ifPresent(des -> {
            Validators.requireNotBlank(des, "channelDescription");
            channel.updateChannelDescription(des);
        });

        Channel savedChannel = channelRepository.save(channel);

        return toDto(savedChannel, getLastMessageAt(savedChannel.getId()));
    }



    @Override
    public void deleteChannel(UUID id) {
        validateExistenceChannel(id);
        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        if (channel.getJoinedUserIds().contains(userId)) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUserIds().add(userId);
        user.getJoinedChannelIds().add(channelId);

        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = validateExistenceChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        if (!channel.getJoinedUserIds().contains(userId)) {
            throw new IllegalArgumentException("채널에 속해 있지 않은 유저입니다.");
        }

        channel.getJoinedUserIds().remove(userId);
        user.getJoinedChannelIds().remove(channelId);
        channelRepository.save(channel);
        userRepository.save(user);
    }


    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }

    private Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findLatestCreatedAtByChannelId(channelId)
                .orElse(null);
    }


    public static ChannelResponseDto toDto(Channel channel, Instant lastMessageAt) {
        List<UUID> joinedUserIds =
                channel.getType() == ChannelType.PRIVATE ? channel.getJoinedUserIds() : null;
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getChannelName(),
                channel.getChannelDescription(),
                lastMessageAt,
                joinedUserIds
        );
    }
}