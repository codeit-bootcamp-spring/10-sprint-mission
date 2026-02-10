package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
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

        // UserRepository는 Optional 미사용: 없으면 null
        List<User> participants = req.participantIds().stream()
                .map(id -> {
                    User u = userRepository.findById(id);
                    if (u == null) throw new UserNotFoundException();
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

        // findChannel은 구현체에서 ChannelNotFoundException 던지는 형태로 맞춤
        Channel channel = channelRepository.findChannel(channelId);

        Instant lastMessageTime = messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // 규칙: PRIVATE만 participantIds 포함, PUBLIC은 null
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

        // user 존재 확인(없으면 예외)
        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
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

        Channel channel = channelRepository.findChannel(channelId);

        User user = userRepository.findById(userId);
        if (user == null) throw new UserNotFoundException();

        if (channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new AlreadyJoinedChannelException();
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

        Channel channel = channelRepository.findChannel(req.channelId());

        if (channel.isPrivate()) {
            // 요구사항: PRIVATE 채널 수정 불가
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.updateChannel(req.name(), req.description());
        channelRepository.saveChannel(channel);

        return find(req.channelId());
    }

    @Override
    public void delete(UUID channelId) {
        requireNonNull(channelId, "channelId");

        // 존재 확인(없으면 예외)
        channelRepository.findChannel(channelId);

        messageRepository.findAllByChannelId(channelId)
                .forEach(m -> messageRepository.delete(m.getId()));

        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteChannel(channelId);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        requireNonNull(channelId, "channelId");
        requireNonNull(userId, "userId");

        Channel channel = channelRepository.findChannel(channelId);

        // 존재 확인
        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }

        boolean removed = channel.getParticipants().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            throw new UserNotInChannelException();
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

        Channel channel = channelRepository.findChannel(channelId);
        return channel.getParticipants().stream().map(User::getId).toList();
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;

        // findChannel은 예외를 던지므로 안전하게 확인
        try {
            channelRepository.findChannel(channelId);
            return true;
        } catch (ChannelNotFoundException e) {
            return false;
        }
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}