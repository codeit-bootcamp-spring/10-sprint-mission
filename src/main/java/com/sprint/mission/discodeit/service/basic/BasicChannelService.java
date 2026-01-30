package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    // TODO: 추후 각 파일 리포지토리 고도화 시에 @Autowired 제거
    @Autowired(required = false)
    private ReadStatusRepository readStatusRepository;

    @Override
    public ChannelDto.Response createPublicChannel(ChannelDto.CreatePublicRequest request) {
        Channel newChannel = new Channel(
                request.name(),
                request.description(),
                ChannelType.PUBLIC
        );
        return convertToResponse(channelRepository.save(newChannel));
    }

    @Override
    public ChannelDto.Response createPrivateChannel(ChannelDto.CreatePrivateRequest request) {
        Channel newChannel = new Channel(
                null,
                null,
                ChannelType.PRIVATE
        );
        Channel saved = channelRepository.save(newChannel);

        request.memberIds().forEach(userId -> {
            readStatusRepository.save(new ReadStatus(userId, saved.getId(), Instant.now()));
            saved.addMember(userId);
            });
        channelRepository.save(saved);
        return convertToResponse(saved);
    }

    @Override
    public ChannelDto.Response findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
        return convertToResponse(channel);
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC ||
                        isMember(userId, c.getId()))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ChannelDto.Response update(ChannelDto.UpdateRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(
                request.name(),
                request.description()
        );
        return convertToResponse(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        messageRepository.findAll().stream()
                .filter(m -> channel.getId().equals(m.getChannelId()))
                .forEach(m -> messageRepository.deleteById(m.getId()));

        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .forEach(rs -> readStatusRepository.delete(rs.getId()));

        channelRepository.deleteById(channelId);
    }

    @Override
    public void addChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (isMember(userId, channelId)) {
            throw new IllegalStateException("이미 가입된 채널입니다.");
        }

        if (readStatusRepository != null) {
            readStatusRepository.save(new ReadStatus(userId, channelId, Instant.now()));
        }

        channel.addMember(userId);
        channelRepository.save(channel);
    }

    @Override
    public void removeChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (readStatusRepository != null) {
            readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                    .findFirst()
                    .ifPresent(rs -> readStatusRepository.delete(rs.getId()));
        }
        channel.removeMember(userId);
        channelRepository.save(channel);
    }

    private boolean isMember(UUID userId, UUID channelId) {
        return readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId));
    }
    // [헬퍼 메서드]: 엔티티를 Response DTO로 변환 (가장 최신 메시지 시간 포함)
    private ChannelDto.Response convertToResponse(Channel channel) {
        // [추가] 요구사항: 해당 채널의 가장 최근 메시지 시간 정보 포함
        Instant lastMessageAt = messageRepository.findLatestMessageTimeByChannelId(channel.getId())
                .orElse(null);

        // [추가] 요구사항: PRIVATE인 경우 참여한 User ID 정보 포함
        List<UUID> memberIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            memberIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelDto.Response(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageAt,
                memberIds
        );
    }
}
