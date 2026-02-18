package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.UpdateChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
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
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto createPublic(CreatePublicChannelRequestDto dto) {
        Channel channel = new Channel(ChannelType.PUBLIC, dto.getName(), dto.getDescription());
        channelRepository.save(channel);
        // 공개 채널은 참여자 목록 없음
        return new ChannelDto(channel, null, List.of());
    }

    @Override
    public ChannelDto createPrivate(CreatePrivateChannelRequestDto dto) {
        Channel channel = new Channel(ChannelType.PRIVATE, dto.getName(), null);
        channelRepository.save(channel);

        if (dto.getParticipantIds() != null) {
            for (UUID userId : dto.getParticipantIds()) {
                ReadStatus status = new ReadStatus(userId, channel.getId());
                readStatusRepository.save(status);
            }
        }

        return channelMapper.toDto(channel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = getChannelEntity(channelId);
        return channelMapper.toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }
                    // private인 경우 내 ReadStatus가 있는지 확인
                    return readStatusRepository.findAllByUserId(userId).stream()
                            .anyMatch(rs -> rs.getChannelId().equals(channel.getId()));
                })
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto> findAll() {
        return List.of(); // 사용하지 않음
    }

    @Override
    public ChannelDto update(UUID channelId, UpdateChannelRequestDto dto) {
        Channel channel = getChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        channel.update(dto.getName(), dto.getDescription());
        channelRepository.save(channel);

        return channelMapper.toDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        getChannelEntity(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    // 채널 엔티티 조회
    private Channel getChannelEntity(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + channelId));
    }

}
