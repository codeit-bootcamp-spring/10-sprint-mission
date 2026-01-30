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

    // PRIVATE 채널 생성
    @Override
    public Channel createPrivate(PrivateChannelCreateRequest privateChReq) {
        if (privateChReq == null || privateChReq.participantIds() == null || privateChReq.participantIds().isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");
        }

        List<User> participants = privateChReq.participantIds().stream()
                .map(id -> userRepository.findById(id).orElseThrow(UserNotFoundException::new))
                .toList();

        Channel channel = new Channel(participants);
        Channel savedChannel = channelRepository.createChannel(channel);

        // 참여자별 ReadStatus 생성
        participants.forEach(user ->
                readStatusRepository.save(new ReadStatus(user.getId(), savedChannel.getId()))
        );

        return savedChannel;
    }

    // PUBLIC 채널 생성
    @Override
    public Channel createPublic(PublicChannelCreateRequest publicChReq) {
        if (publicChReq == null || publicChReq.name() == null || publicChReq.name().isBlank()) {
            throw new IllegalArgumentException("PUBLIC 채널은 이름이 필요합니다.");
        }

        // PUBLIC 채널 기존 로직 유지
        Channel channel = new Channel(publicChReq.name(), publicChReq.description());
        return channelRepository.createChannel(channel);
    }

    // 특정 채널 조회
    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findChannel(channelId);
    }

    // 특정 User가 볼 수 있는 채널 목록 조회
    // PUBLIC: 전체
    // PRIVATE: 해당 userId가 참여한 채널만
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAllChannel().stream()
                .filter(channel ->
                        !channel.isPrivate()
                                || channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))
                )
                .map(channel -> {
                    List<UUID> participantIds = channel.isPrivate()
                            ? channel.getParticipants().stream().map(User::getId).toList()
                            : List.of();

                    // 최근 메시지 시간 계산 (Service에서)
                    Instant lastMessageTime = messageRepository.findAllByChannelId(channel.getId()).stream()
                            .map(Message::getCreatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

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

    // 채널에 사용자 추가
    @Override
    public Channel addUserInChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannel(channelId);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new AlreadyJoinedChannelException();
        }

        channel.addParticipant(user);

        // PRIVATE 채널이면 ReadStatus도 생성
        if (channel.isPrivate()) {
            readStatusRepository.save(new ReadStatus(user.getId(), channel.getId()));
        }

        return channelRepository.saveChannel(channel);
    }

    // 채널 업데이트 (PRIVATE 채널 수정 불가)
    @Override
    public Channel update(ChannelUpdateRequest req) {
        Channel channel = channelRepository.findChannel(req.channelId());

        if (channel.isPrivate()) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.updateChannel(req.name(), req.description());
        return channelRepository.saveChannel(channel);
    }

    // 채널 삭제 (Message, ReadStatus 같이 삭제)
    @Override
    public void delete(UUID channelId) {
        channelRepository.findChannel(channelId); // 없으면 예외

        messageRepository.findAllByChannelId(channelId)
                .forEach(m -> messageRepository.delete(m.getId()));
        readStatusRepository.deleteByChannelId(channelId);

        channelRepository.deleteChannel(channelId);
    }

    // 채널에서 유저 나가기
    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannel(channelId);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        boolean removed = channel.getParticipants().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            throw new UserNotInChannelException();
        }

        // PRIVATE 채널이면 ReadStatus 삭제
        if (channel.isPrivate()) {
            ReadStatus readStatus =
                    readStatusRepository.findByUserIdAndChannelId(userId, channelId);

            if (readStatus != null) {
                readStatusRepository.delete(readStatus.getId());
            }
        }

        channelRepository.saveChannel(channel);
    }

    // 채널 참가자 목록
    @Override
    public List<UUID> findAllUserInChannel(UUID channelId) {
        Channel channel = channelRepository.findChannel(channelId);
        return channel.getParticipants().stream().map(User::getId).toList();
    }

    @Override
    public boolean existsById(UUID channelId) {
        try {
            channelRepository.findChannel(channelId);
            return true;
        } catch (ChannelNotFoundException e) {
            return false;
        }
    }
}
