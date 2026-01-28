package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService{
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 채널 생성
    @Override
    public Channel create(String name, String description, String type, boolean isPublic) {
        Channel newChannel = new Channel(name, description, type, isPublic);
        return channelRepository.save(newChannel);
    }

    // 채널 ID로 조회
    @Override
    public Channel findById(UUID id){
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널 ID입니다."));
    }

    // 채널 전부 조회
    @Override
    public List<Channel> findAll(){
        return channelRepository.findAll();
    }

    // 채널 정보 수정
    @Override
    public Channel update(UUID id, String name, String description, Boolean isPublic){
        Channel channel = findById(id);

        Optional.ofNullable(name).ifPresent(channel::updateName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        Optional.ofNullable(isPublic).ifPresent(channel::updateIsPublic);

        channelRepository.save(channel);

        for (User member : channel.getMembers()) {
            userRepository.save(member);
        }

        for (Message message : channel.getMessages()) {
            messageRepository.save(message);
        }

        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id){
        Channel channel = findById(id);

        for (User member : channel.getMembers()) {
            member.leaveChannel(channel);
            userRepository.save(member);
        }

        for (Message message : channel.getMessages()) {
            messageRepository.delete(message);
        }

        channelRepository.delete(channel);
    }

    // 채널 참가
    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = findUserOrThrow(userId);

        if (channel.isMember(user)) {
            throw new IllegalStateException("이미 채널에 가입된 유저입니다.");
        }

        channel.addMember(user);
        user.addJoinedChannel(channel);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    // 채널 탈퇴
    @Override
    public void leave(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = findUserOrThrow(userId);

        if (!channel.isMember(user)) {
            throw new IllegalStateException("해당 채널의 멤버가 아닙니다.");
        }

        channel.removeMember(user);
        user.leaveChannel(channel);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    // 특정 채널의 유저 목록 조회
    @Override
    public List<User> findMembersByChannelId(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getMembers();
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId){
        Channel channel = findById(channelId);
        return channel.getMessages();
    }

    // 헬퍼 메서드 - 저장소에서 유저 조회
    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));
    }
}
