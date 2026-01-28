package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    // field
    private final List<User> userData;
    private MessageService messageService;
    private ChannelService channelService;

    // constructor
    public JCFUserService() {
        // [저장]
        this.userData = new ArrayList<>();
    }

    // Setter
    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setChannelService(ChannelService channelService) {this.channelService = channelService;}

    // User 등록 , 저장 로직
    @Override
    public User create(String name) {
        User user = new User(name);
        // [저장]
        this.userData.add(user);

        return user;
    }

    // 단건 조회, 비즈니스 로직
    @Override
    public User find(UUID userID){
        // [저장]
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
    }

    // 다건 조회, 비즈니스 로직
    @Override
    public List<User> findAll(){
        // [저장]
        return userData;
    }

    // User 수정, 비즈니스 로직
    @Override
    public User updateName(UUID id, String name){
        // [저장]
        User user = find(id);
        // [비즈니스]
        user.updateName(name);
        return user;
    }

    // User 삭제 , 저장 로직
    @Override
    public void deleteUser(UUID userID){
        if (messageService == null) {
            throw new IllegalStateException("MessageService is not set in JCFUserService");
        }
        // [저장]
        User user = find(userID);

        // [비즈니스] User가 보낸 Message 삭제
        List<Message> messageList = new ArrayList<>(user.getMessageList());
        messageList.forEach(message -> messageService.deleteMessage(message.getId()));

        // [비즈니스] Channel에서 User 탈퇴 및 User가 가입한 channel에서 User 탈퇴 , 양방향 삭제를 해줘야 객체가 완전히 지워짐 ??
        List<Channel> channels = new ArrayList<>(user.getChannels());
        channels.forEach(channel -> {
            channel.removeMember(user);
            user.leaveChannel(channel);
        });

        // [저장] userData에서 user 완전 삭제
        userData.remove(user);
    }

    // User가 가입한 전체 Channel 조회 , 비즈니스 로직
    @Override
    public List<String> findJoinedChannels(UUID userID){
        // [저장]
        User user = find(userID);
        return user.getChannels().stream()
                .map(Channel::getName)
                .collect(Collectors.toList());
    }

}
