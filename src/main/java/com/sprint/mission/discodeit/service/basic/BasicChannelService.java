package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    //
    private final ChannelMapper channelMapper;

//    @Override
//    private Channel create(ChannelDto.CreatePublicRequest request) {
//        String channelName = request.name();
//        String channelDescription = request.description();
//        Channel channel = new Channel(ChannelType.PUBLIC, channelName, channelDescription);
//        return channelRepository.save(channel);
//    }
//
//    @Override
//    private Channel create(ChannelDto.CreatePrivateRequest request) {
//        Set<UUID> memberIds = new HashSet<>(request.memberIds()); // 유저 중복 검출
//
//        memberIds.forEach(userId -> // 멤버가 실제로 존재하는 유저인지 확인
//                userRepository.findById(userId)
//                            .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다." + userId)));
//
//        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
//        Channel createChannel = channelRepository.save(channel);
//
//        request.memberIds().stream()
//                .map(userId -> new ReadStatus(userId, createChannel.getId(), createChannel.getCreatedAt()))
//                .forEach(readStatusRepository::save);
//
//        return createChannel;
//    }

    @Transactional
    @Override
    public ChannelDto.Response create(ChannelDto.CreateRequest request) {
        if (request.type() == ChannelType.PUBLIC) {
            return createPublic(request.name(), request.description());
        }
        else if (request.type() == ChannelType.PRIVATE) {
            return createPrivate(request.memberIds());
        }
        else  {
            throw new IllegalArgumentException("잘못된 채널 타입: " + request.type());
        }
    }

    private ChannelDto.Response createPublic(String name, String description) {
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        return toDto(channelRepository.save(channel));
    }

    private ChannelDto.Response createPrivate(Set<UUID> memberIds) {
        memberIds.forEach(userId -> // 멤버가 실제로 존재하는 유저인지 확인
                userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다." + userId)));

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createChannel = channelRepository.save(channel);

        memberIds.stream()
                .map(userId -> new ReadStatus(userId, createChannel.getId(), createChannel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return toDto(createChannel);
    }

    @Override
    public ChannelDto.Response find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channelId));
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다." + userId);
        }

        // 유저가 가입한 private 채널 목록
        Set<UUID> userJoinedPrivateChannelIds =
                readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        // 모든 채널에서
        return channelRepository.findAll().stream()
                .filter(channel -> // 아래의 조건이 참이면 가져옴
                        channel.getType() == ChannelType.PUBLIC // 채널이 public이거나
                                || userJoinedPrivateChannelIds.contains(channel.getId()) // 유저가 가입한 private 채널이거나
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelDto.Response update(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 찾을 수 없습니다: " + channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다:" + channelId);
        }

        channel.update(request.newName(), request.newDescription());

        return toDto(channelRepository.save(channel));
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널을 찾을 수 없습니다:" + channelId);
        }

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    // Helper
    private ChannelDto.Response toDto(Channel channel) {
        List<UUID> memberIds = new ArrayList<>();

        if (channel.getType() == ChannelType.PRIVATE) { // private 채널일 경우
            memberIds = readStatusRepository.findAllByChannelId(channel.getId()) // 채널 id에 맞는 readStatus 전부 가져옴
                    .stream()
                    .map(ReadStatus::getUserId) // 채널 id를 가지고 있는 readStatus의 유저 id를 전부 가져옴, 즉 비공개 채널의 멤버 가져옴
                    .toList();
        } else {
            memberIds = List.of();
        }

        return channelMapper.toResponse(channel, memberIds);
    }

}
