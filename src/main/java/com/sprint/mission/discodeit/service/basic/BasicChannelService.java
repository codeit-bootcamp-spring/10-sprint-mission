package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        channelRepository.save(channel);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user,channel, Instant.now());
            readStatusRepository.save(readStatus);
        }
        return channel;
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.users().stream()
                .map(user -> new ReadStatus(user, createdChannel, channel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new ChannelNotFoundException(channelId + " 에 해당하는 채널이 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<Channel> mySubscribedChannels = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannels.contains(channel)
                )
                .map(channel -> {

                    ChannelDto baseDto = channelMapper.toDto(channel);

                    List<UserDto> participants =
                            readStatusRepository.findAllByChannelId(channel.getId()).stream()
                                    .map(ReadStatus::getUser)
                                    .map(userMapper::toDto)
                                    .toList();

                    return new ChannelDto(
                            baseDto.id(),
                            baseDto.type(),
                            baseDto.name(),
                            baseDto.description(),
                            participants,
                            null
                    );
                })
                .toList();
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("Channel with" + channelId + " not found."));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }
        channel.update(newName, newDescription);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("Channel with" + channelId + " not found."));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        ChannelDto baseDto = channelMapper.toDto(channel);

        Instant lastMessageAt = messageRepository.
                findAllByChannelId(channel.getId(), PageRequest.of(0, 50, Sort.by("createdAt").descending()))
                .getContent()
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UserDto> participants = new ArrayList<>();

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .forEach(participants::add);
        }

        return new ChannelDto(
                baseDto.id(),
                baseDto.type(),
                baseDto.name(),
                baseDto.description(),
                participants,
                lastMessageAt
        );
    }
}
