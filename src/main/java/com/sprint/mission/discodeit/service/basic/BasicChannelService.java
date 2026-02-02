package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;


    @Override
    public ChannelDto.response createChannel(ChannelDto.createPrivateRequest channelPrivateReq) {
        // title과 description 불필요에 따른 title 미검증
        Channel channel = new Channel(channelPrivateReq.channelType(), null, null);
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public ChannelDto.response createChannel(ChannelDto.createPublicRequest channelPublicReq) {
        validateDuplicateTitle(channelPublicReq.title());

        Channel channel = new Channel(channelPublicReq.channelType(),
                channelPublicReq.title(), channelPublicReq.description());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public ChannelDto.response findChannel(UUID uuid) {
        return channelRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    @Override
    public ChannelDto.response findChannelByTitle(String title) {
        return channelRepository.findAll().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .map(this::toResponse)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    @Override
    public List<ChannelDto.response> findAllByUserId(UUID userId) {
        getUserOrThrow(userId);

        return channelRepository.findAll().stream()
                // PUBLIC 채널 전부 + userId가 참여한 PRIVATE 채널
                .filter(c -> Objects.equals(c.getChannelType(), ChannelType.PUBLIC)
                                || c.getParticipants().contains(userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelDto.response updateChannel(UUID uuid, ChannelDto.updatePublicRequest channelReq) {
        Channel channel = getChannelOrThrow(uuid);

        // title 중복성 검사
        if (channelReq.title() != null && !Objects.equals(channel.getTitle(), channelReq.title()))
            validateDuplicateTitle(channelReq.title());

        Optional.ofNullable(channelReq.title()).ifPresent(channel::updateTitle);
        Optional.ofNullable(channelReq.description()).ifPresent(channel::updateDescription);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        return toResponse(channel);
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Channel channel = getChannelOrThrow(uuid);

        readStatusRepository.deleteAllByChannelId(channel.getId());
        messageRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(uuid);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelOrThrow(channelId);
        User user = getUserOrThrow(userId);

        if (channel.getParticipants().stream()
                .anyMatch(u -> Objects.equals(u, userId))) {
            throw new IllegalStateException("이미 참가한 참가자입니다");
        }

        if (user.getJoinedChannels().stream()
                .anyMatch(u -> Objects.equals(u, channelId))) {
            throw new IllegalStateException("이미 참가한 채널입니다");
        }

        channel.addParticipant(userId);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        user.addJoinedChannels(channelId);
        user.updateUpdatedAt();
        userRepository.save(user);

        ReadStatus readStatus = new ReadStatus(userId, channelId);
        readStatusRepository.save(readStatus);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelOrThrow(channelId);
        User user = getUserOrThrow(userId);

        if (channel.getParticipants().stream()
                .anyMatch(u -> Objects.equals(u, userId))) {
            throw new IllegalStateException("참여하지 않은 참가자입니다");
        }

        if (user.getJoinedChannels().stream()
                .anyMatch(u -> Objects.equals(u, channelId))) {
            throw new IllegalStateException("참가하지 않은 채널입니다");
        }

        channel.removeParticipant(userId);
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        user.removeJoinedChannels(channelId);
        user.updateUpdatedAt();
        userRepository.save(user);

        ReadStatus readStatus = readStatusRepository.findAllByChannelId(channelId).stream()
                .filter(r -> Objects.equals(r.getUserId(), userId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("readStatus가 존재하지 않습니다"));
        readStatusRepository.deleteById(readStatus.getId());
    }

    private void validateDuplicateTitle(String title) {
        channelRepository.findAll().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .findFirst()
                .ifPresent(u -> { throw new IllegalStateException("이미 존재하는 채널명입니다"); });
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    private ChannelDto.response toResponse(Channel channel) {
        Instant lastMessageAt  = messageRepository.findAllByChannelId(channel.getId()).stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt).reversed())
                .limit(1)
                .findFirst()
                .map(BaseEntity::getCreatedAt)
                .orElse(null);

        return new ChannelDto.response(channel.getId(), channel.getCreatedAt(), channel.getUpdatedAt(),
                channel.getChannelType(), channel.getTitle(), channel.getDescription(),
                lastMessageAt,
                channel.getParticipants().stream().toList());

    }
}
