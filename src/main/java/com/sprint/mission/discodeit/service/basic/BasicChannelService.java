package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    // 필드
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        // [저장]
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID id) {
        return channelRepository.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateName(UUID channelID, String name) {
        // [저장] , 조회
        Channel channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
        // 비즈니스
        channel.updateName(name);
        channelRepository.save(channel);

        Set<UUID> userIds = new HashSet<>();
        // user에서도 변경된 이름으로 save되어야 함.
        for (User user : channel.getMembersList()) {
            for (Channel c : user.getChannelsList()) {
                if (c.getId().equals(channelID)) {
                    c.updateName(name);
                    userIds.add(user.getId());
                    break;
                }
            }
        }
        // 변경사항 저장 -> find에서 load하고
        for (UUID userId : userIds) {
            userRepository.save(userRepository.find(userId).get());
        }

        Set<UUID> messageIds = new HashSet<>();


        for (Message message : channel.getMessageList()) {
            message.getChannel().updateName(name);
            messageIds.add(message.getId());
        }

        // 변경사항 저장
        for (UUID messageId : messageIds) {
            messageRepository.save(messageRepository.find(messageId).get());
        }

        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelID) {
        // [저장]
        Channel channel = find(channelID);

        // [비즈니스]
        List<User> members = new ArrayList<>(channel.getMembersList());

        // [비즈니스], 여기 삭제가 잘 되려나 ??
        members.forEach(user -> user.leaveChannel(channel));

        // [비즈니스]
        List<Message> messageList = new ArrayList<>(channel.getMessageList());
        messageList.forEach(message -> messageRepository.deleteMessage(message.getId()));

        // [저장]
        channelRepository.deleteChannel(channel.getId());
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {

        // [저장], 조회
        Channel channel = find(channelID);
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        if (channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is already in this channel." + channelID);
        }

        // [비즈니스]
        channel.addMember(user);

        // [비즈니스]
        user.joinChannel(channel);

        // [저장], 변경사항 저장
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        Channel channel = find(channelID);
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // 객체 contains(user) 금지 -> id 기준으로 멤버십 확인
        boolean isMember = channel.getMembersList().stream()
                .anyMatch(u -> u.getId().equals(userID));

        if (!isMember) {
            throw new IllegalArgumentException("User is not in this channel." + channelID);
        }

        // 객체 remove(user) 금지 -> id 기준 제거
        channel.getMembersList().removeIf(u -> u.getId().equals(userID));

        // 객체 remove(channel) 금지 -> id 기준 제거
        user.getChannelsList().removeIf(c -> c.getId().equals(channelID));

        // 기존 삭제의 원인 (채널/메시지/유저 연쇄 삭제 원인) -> User가 해당 Channel에 보낸 메시지도 삭제되어야 하지 않나?
        // List<Message> messageList = new ArrayList<>(user.getMessageList());
        // messageList.stream()
        //        .filter(msg -> msg.getChannel().equals(channel))
        //        .forEach(msg -> messageService.deleteMessage(msg.getId()));

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public List<String> findMembers(UUID channelID) {
        Channel channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
        // userService의 find로 user 객체 최신화 필요
        return channel.getMembersList().stream()
                .map(user -> userRepository.find(user.getId())
                        .orElseThrow(()-> new IllegalArgumentException("User not found: "+ user.getId())))
                .map(User::getName)
                .collect(java.util.stream.Collectors.toList());
    }
}
