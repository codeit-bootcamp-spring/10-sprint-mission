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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(ChannelDto.CreatePublicRequest request) {
        String channelName = request.name();
        String channelDescription = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, channelName, channelDescription);
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(ChannelDto.CreatePrivateRequest request) {
        request.memberIds() // 채널 멤버가 실제로 있는 유저인지 확인
                .forEach(userId -> {
                    userRepository.findById(userId)
                            .orElseThrow(NoSuchElementException::new);
                        }

                );

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createChannel = channelRepository.save(channel);

        request.memberIds().stream()
                .map(userId -> new ReadStatus(userId, createChannel.getId(), createChannel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return createChannel;
    }

    @Override
    public ChannelDto.Response find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::mapToDto)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channelId));
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {


        // 유저가 가입한 private 채널 목록
        Set<UUID> userJoinedPrivateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        // 모든 채널에서
        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC // 채널이 public이거나
                                || userJoinedPrivateChannelIds.contains(channel.getId()) // 유저가 가입한 private 채널이거나
                )
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public Channel update(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PUBLIC) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다:" + channelId);
        }

        channel.update(request.newName(),  request.newDescription());

        return channelRepository.save(channel);
    }
    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);

        channelRepository.deleteById(channelId);


    }

    // Helper
    private ChannelDto.Response mapToDto(Channel channel) {
        List<UUID> memberIds = new ArrayList<>();

        if (channel.getType() == ChannelType.PRIVATE) { // private 채널일 경우
            memberIds = readStatusRepository.findAllByChannelId(channel.getId()) // 채널 id에 맞는 readstatus 전부 가져옴
                    .stream()
                    .map(ReadStatus::getUserId) // 채널 id를 가지고 있는 readStatus의 유저 id를 전부 가져옴, 즉 비공개 채널의 멤버 가져옴
                    .toList();
        } else {
            memberIds = List.of();
        }

        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()) // 채널id에 해당하는 메시지 전부 가져옴
                .stream()
                .max(Comparator.comparing(Message::getCreatedAt)) // 생성 시간이 가장 큰 메시지 가져옴
                .map(Message::getCreatedAt)
                .orElse(null);

        return ChannelDto.Response.from(channel, memberIds, lastMessageAt);
    }


}
