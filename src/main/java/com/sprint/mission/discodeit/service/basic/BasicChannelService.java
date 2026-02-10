package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UUID createPrivate(PrivateChannelCreateRequest req) {
        requireNonNull(req, "privateChReq");
        requireNonNull(req.participantIds(), "participantIds");
        if (req.participantIds().isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");
        }

        List<User> participants = req.participantIds().stream()
                .map(id -> {
                    User u = userRepository.findById(id);
                    if (u == null) throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
                    return u;
                })
                .toList();

        Channel channel = new Channel(participants);
        UUID channelId = channelRepository.createChannel(channel);

        participants.forEach(user -> {
            ReadStatus rs = new ReadStatus(user.getId(), channelId);
            rs.updateLastReadAt(Instant.now());
            readStatusRepository.save(rs);
        });

        return channelId;
    }

    @Override
    public UUID createPublic(PublicChannelCreateRequest req) {
        requireNonNull(req, "publicChReq");
        requireNonNull(req.name(), "name");
        if (req.name().isBlank()) {
            throw new IllegalArgumentException("PUBLIC 채널은 이름이 필요합니다.");
        }

        Channel channel = new Channel(req.name(), req.description());
        return channelRepository.createChannel(channel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        requireNonNull(channelId, "channelId");

        Channel channel = findChannelOrThrow(channelId);

        Instant lastMessageTime = messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = channel.isPrivate()
                ? channel.getParticipants().stream().map(User::getId).toList()
                : null;

        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.isPrivate(),
                lastMessageTime,
                participantIds
        );
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        if (userRepository.findById(userId) == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        return channelRepository.findAllChannel().stream()
                .filter(channel ->
                        !channel.isPrivate()
                                || channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))
                )
                .map(channel -> {
                    Instant lastMessageTime = messageRepository.findAllByChannelId(channel.getId()).stream()
                            .map(Message::getCreatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    List<UUID> participantIds = channel.isPrivate()
                            ? channel.getParticipants().stream().map(User::getId).toList()
                            : null;

                    return new ChannelResponse(
                            channel.getId(),
                            channel.getChannelName(),
                            channel.getDescription(),
                            channel.isPrivate(),
                            lastMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    @Override
    public Channel addUserInChannel(UUID channelId, UUID userId) {
        requireNonNull(channelId, "channelId");
        requireNonNull(userId, "userId");

        Channel channel = findChannelOrThrow(channelId);

        User user = userRepository.findById(userId);
        if (user == null) throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);

        if (channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new BusinessLogicException(ErrorCode.ALREADY_JOINED_CHANNEL);
        }

        channel.addParticipant(user);

        if (channel.isPrivate()) {
            ReadStatus rs = new ReadStatus(user.getId(), channelId);
            rs.updateLastReadAt(Instant.now());
            readStatusRepository.save(rs);
        }

        return channelRepository.saveChannel(channel);
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest req) {
        requireNonNull(req, "req");
        requireNonNull(req.channelId(), "channelId");

        Channel channel = findChannelOrThrow(req.channelId());

        if (channel.isPrivate()) {
            throw new BusinessLogicException(ErrorCode.CONFLICT);
        }

        channel.updateChannel(req.name(), req.description());
        channelRepository.saveChannel(channel);

        return find(req.channelId());
    }

    @Override
    public void delete(UUID channelId) {
        requireNonNull(channelId, "channelId");

        findChannelOrThrow(channelId);

        messageRepository.findAllByChannelId(channelId)
                .forEach(m -> messageRepository.delete(m.getId()));

        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteChannel(channelId);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        requireNonNull(channelId, "channelId");
        requireNonNull(userId, "userId");

        Channel channel = findChannelOrThrow(channelId);

        if (userRepository.findById(userId) == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        boolean removed = channel.getParticipants().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_IN_CHANNEL);
        }

        if (channel.isPrivate()) {
            ReadStatus rs = readStatusRepository.findByUserIdAndChannelId(userId, channelId);
            if (rs != null) {
                readStatusRepository.delete(rs.getId());
            }
        }

        channelRepository.saveChannel(channel);
    }

    @Override
    public List<UUID> findAllUserInChannel(UUID channelId) {
        requireNonNull(channelId, "channelId");

        Channel channel = findChannelOrThrow(channelId);
        return channel.getParticipants().stream().map(User::getId).toList();
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return channelRepository.findChannel(channelId) != null;
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Channel channel = channelRepository.findChannel(channelId);
        if (channel == null) {
            throw new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND);
        }
        return channel;
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
