package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private ChannelService channelService;
    private MessageService messageService;
    private final List<User> data = new ArrayList<>();

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User createUser(String userName, String password, String email) {
        validateUserExist(userName);
        User user = new User(userName, password, email);
        data.add(user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(data);
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        List<User> result = new ArrayList<>();
        channelService.getChannel(channelId)
                .getUserIds()
                .forEach(userId -> result.add(getUser(userId)));
        return result;
    }

    @Override
    public User updateUser(UUID userId, String userName, String password, String email) {
        validateUserExist(userName);
        User findUser = getUser(userId);
        Optional.ofNullable(userName)
                .ifPresent(findUser::updateUserName);
        Optional.ofNullable(password)
                .ifPresent(findUser::updatePassword);
        Optional.ofNullable(email)
                .ifPresent(findUser::updateEmail);
        return findUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        Optional<User> deleteUser = data.stream()
                .filter(user -> user.getId().equals(userId))
                .findAny();
        if(deleteUser.isEmpty()) return;
        User target = deleteUser.get();
        target.getMessageIds().forEach(messageId -> messageService.deleteMessage(messageId));
        target.getChannelIds().forEach(channelId -> channelService.leaveChannel(channelId, userId));
        data.remove(target);
    }

    private void validateUserExist(String userName) {
        if(data.stream().anyMatch(u -> u.getUserName().equals(userName)))
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
    }
}
