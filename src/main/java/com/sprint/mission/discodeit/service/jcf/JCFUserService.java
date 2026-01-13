package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users;

    public JCFUserService() {
        users = new HashMap<>();
    }

    // 외부에서 객체를 받는 것 보다는 메소드 내부에서 객체 생성해서 반환
    @Override
    public User createUser(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("생성하고자 하는 유저의 이름이 null일 수 없음");
        }
        if (userName.isEmpty()) {
            throw new IllegalArgumentException("생성하고자 하는 유저의 이름이 빈문자열일 수 없음");
        }
        User user = new User(userName.trim());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalStateException("해당 id의 사용자를 찾을 수 없음");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateById(UUID id, String newUserName) {
        User targetUser = findById(id);
        if (newUserName == null) {
            throw new IllegalArgumentException("변경 하려는 유저의 이름이 null일 수 없음");
        }
        targetUser.setUserName(newUserName);
        return targetUser;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        users.remove(id);
    }

    // 해당 id를 가진 유저가 속한 채널 목록을 반환
    public List<Channel> getChannelsById(UUID id) {
        User user = findById(id);
        List<Channel> channels = user.getChannels();
        if (channels.isEmpty()) {
            throw new IllegalStateException("유저가 속한 채널이 없습니다.");
        }
        return channels;
    }

    // 해당 id를 가진 유저가 작성한 메시지 목록을 반환
    public List<Message> getMessagesById(UUID id) {
        User user = findById(id);
        List<Message> messages = user.getMessages();
        if (messages.isEmpty()) {
            throw new IllegalStateException("유저가 작성한 메시지가 없습니다.");
        }
        return messages;
    }
}
