package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    public PublicChannelInfo createPublicChannel(PublicChannelCreateInfo channelInfo) {
        validateChannelExist(channelInfo.channelName());
        Channel channel = new Channel(channelInfo.channelName(), ChannelType.PUBLIC, channelInfo.description());
        channelRepository.save(channel);
        return ChannelMapper.toPublicChannelInfo(channel);
    }

    public PrivateChannelInfo createPrivateChannel(PrivateChannelCreateInfo channelInfo) {
        Channel channel = new Channel(null, ChannelType.PRIVATE, null);
        channelRepository.save(channel);
        channelInfo.userIds().forEach(userId -> {
            joinChannel(channel.getId(), userId);
            ReadStatus readStatus = new ReadStatus(channel.getId(), userId);
            readStatusRepository.save(readStatus);
        });
        return ChannelMapper.toPrivateChannelInfo(channel);
    }

    @Override
    public ChannelInfo findChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        return ChannelMapper.toChannelInfo(channel, getLastMessageTime(channelId));
    }

    @Override
    public List<ChannelInfo> findAll() {
        return channelRepository.findAll()
                .stream()
                .map(channel ->
                        ChannelMapper.toChannelInfo(channel, getLastMessageTime(channel.getId()))
                )
                .toList();
    }

    @Override
    public List<ChannelInfo> findAllByUserId(UUID userId) {
        return channelRepository.findAll()
                .stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC
                        || (channel.getChannelType() == ChannelType.PRIVATE && channel.getUserIds().contains(userId)))
                .map(channel ->
                        ChannelMapper.toChannelInfo(channel, getLastMessageTime(channel.getId()))
                )
                .toList();
    }

    @Override
    public ChannelInfo updateChannel(UpdateChannelInfo channelInfo) {
        Channel findChannel = channelRepository.findById(channelInfo.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
        if(findChannel.getChannelType() == ChannelType.PRIVATE)
            throw new IllegalStateException("해당 채널은 수정할 수 없습니다.");
        validateChannelExist(channelInfo.channelName());

        Optional.ofNullable(channelInfo.channelName())
                .ifPresent(findChannel::updateChannelName);
        Optional.ofNullable(channelInfo.description())
                .ifPresent(findChannel::updateDescription);

        channelRepository.save(findChannel);

        return ChannelMapper.toChannelInfo(findChannel, getLastMessageTime(findChannel.getId()));
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
        userRepository.findAllByChannelId(channelId).forEach(user -> {
                user.removeChannelId(channelId);
                userRepository.save(user);
        });

        messageRepository.findAllByChannelId(channelId).forEach(message ->
                messageRepository.deleteById(message.getId()));

        readStatusRepository.findAllByChannelId(channelId).forEach(readStatus ->
                readStatusRepository.deleteById(readStatus.getId()));

        channelRepository.deleteById(channelId);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        channel.addUserId(userId);
        user.addChannelId(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
        readStatusRepository.save(new ReadStatus(channelId, userId));
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        ReadStatus findReadStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new IllegalStateException("유저가 채널에 가입되어 있지 않습니다."));

        channel.removeUserId(userId);
        user.removeChannelId(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
        readStatusRepository.deleteById(findReadStatus.getId());
    }

    private void validateChannelExist(String channelName) {
        if(channelRepository.findByName(channelName).isPresent())
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
    }

    private Instant getLastMessageTime(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        if(messages.isEmpty()) return null;
        else
            return messages
                .stream()
                .max(Comparator.comparing(Message::getUpdateAt))
                .orElseThrow(() -> new IllegalStateException("메세지가 존재하지 않습니다."))
                .getUpdateAt();
    }
}
