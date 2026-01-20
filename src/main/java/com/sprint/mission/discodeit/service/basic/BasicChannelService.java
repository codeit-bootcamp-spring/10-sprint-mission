package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private MessageService messageService;
    private UserService userService;

    // 생성자를 통해 레포지토리 주입 (DI)
    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // 서비스 간 순환 참조 방지를 위한 Setter 주입
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public Channel createChannel(String name, String description, Channel.ChannelType visibility) {
        Channel newChannel = new Channel(name, description, visibility);
        return channelRepository.save(newChannel);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String newName, String description, Channel.ChannelType newVisibility) {
        Channel channel = findById(channelId);

        // 기존 FileChannelService의 Optional 업데이트 로직 유지
        Optional.ofNullable(newName)
                .filter(name -> !name.equals(channel.getChannelName()))
                .ifPresent(channel::updateName);

        Optional.ofNullable(description)
                .filter(desc -> !desc.equals(channel.getDescription()))
                .ifPresent(channel::updateDescription);

        Optional.ofNullable(newVisibility)
                .filter(v -> v != channel.getChannelVisibility())
                .ifPresent(channel::updateVisibility);

        // 변경사항 저장 (저장 로직 위임)
        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        // 1. 존재 여부 확인 (레포지토리 기능 활용)
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }

        Channel channel = findById(channelId);

        // 2. 비즈니스 로직: 연관된 메시지 삭제
        if (messageService != null) {
            messageService.deleteMessagesByChannelId(channelId);
        }

        // 3. 비즈니스 로직: 가입된 유저들에게서 채널 정보 제거 (Cascade 처리)
        if (userService != null) {
            channel.getUsers().forEach(user -> {
                user.leaveChannel(channel);
                userService.save(user); // 유저 정보 업데이트
            });
        }

        // 4. 저장 로직: 실제 저장소에서 채널 삭제 위임
        channelRepository.deleteById(channelId);
    }
}