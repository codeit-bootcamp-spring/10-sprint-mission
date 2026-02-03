package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponse createChannel(ChannelCreateRequest request) {
        if ("PRIVATE".equals(request.getType())) {
            return channelMapper.toResponse(createPrivateChannel(request));
        } else {
            return channelMapper.toResponse(createPublicChannel(request));
        }
    }

    private Channel createPrivateChannel(ChannelCreateRequest request) {
        Channel channel = new Channel(request.getName(), "PRIVATE", request.getDescription());
        channelRepository.save(channel);

        if (request.getParticipantIds() != null) {
            for (UUID userId : request.getParticipantIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다: " + userId));
                
                channel.addUser(user);
                user.addChannel(channel);
                userRepository.save(user);

                ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
                readStatusRepository.save(readStatus);
            }
        }
        return channelRepository.findById(channel.getId()).orElse(channel);
    }

    private Channel createPublicChannel(ChannelCreateRequest request) {
        if (channelRepository.findAll().stream().anyMatch(c -> c.getName() != null && c.getName().equals(request.getName()))) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다: " + request.getName());
        }

        Channel channel = new Channel(request.getName(), "PUBLIC", request.getDescription());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public ChannelResponse getChannel(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        return channelMapper.toResponse(channel);
    }

    @Override
    public List<ChannelResponse> getAllChannels() {
        return channelRepository.findAll().stream()
                .map(channelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return channelRepository.findAll().stream()
                .filter(channel -> canUserAccessChannel(channel, userId))
                .map(channelMapper::toResponse)
                .sorted(Comparator.comparing(ChannelResponse::getLastMessageAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean canUserAccessChannel(Channel channel, UUID userId) {
        if ("PUBLIC".equals(channel.getType())) {
            return true;
        }
        return readStatusRepository.findByUserIdAndChannelId(userId, channel.getId()).isPresent();
    }

    @Override
    public ChannelResponse updateChannel(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        
        if ("PRIVATE".equals(channel.getType())) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        Optional.ofNullable(request.getName()).filter(n -> !n.isBlank()).ifPresent(channel::updateName);
        Optional.ofNullable(request.getType()).filter(t -> !t.isBlank()).ifPresent(channel::updateType);
        Optional.ofNullable(request.getDescription()).ifPresent(channel::updateDescription);
        
        channelRepository.save(channel);
        return channelMapper.toResponse(channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        for(Message message : new ArrayList<>(channel.getMessages())) {
             messageRepository.delete(message.getId());
        }

        for (User user : new ArrayList<>(channel.getUsers())) {
            user.removeChannel(channel);
            userRepository.save(user);
        }

        readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(id))
                .forEach(rs -> readStatusRepository.delete(rs.getId()));

        channelRepository.delete(id);
    }

    @Override
    public ChannelResponse enterChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if ("PRIVATE".equals(channel.getType())) {
            throw new IllegalStateException("PRIVATE 채널은 초대 없이 입장할 수 없습니다.");
        }

        if (channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
        }

        channel.addUser(user);
        user.addChannel(channel);
        
        channelRepository.save(channel);
        userRepository.save(user);
        
        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);

        return channelMapper.toResponse(channel);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        channel.getMessages().stream()
                .filter(m -> m.getAuthorId().equals(userId))
                .forEach(m -> messageRepository.delete(m.getId()));

        channel.removeUser(user);
        user.removeChannel(channel);

        channelRepository.save(channel);
        userRepository.save(user);
        
         readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId) && rs.getUserId().equals(userId))
                .findFirst()
                .ifPresent(rs -> readStatusRepository.delete(rs.getId()));
    }
}