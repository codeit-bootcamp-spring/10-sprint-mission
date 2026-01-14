package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validator;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users;

    public JCFUserService() {
        users = new HashMap<>();
    }

    // 외부에서 객체를 받는 것 보다는 메소드 내부에서 객체 생성해서 반환
    @Override
    public User createUser(String userName) {
        Validator.validateNotNull(userName, "생성하고자 하는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(userName, "생성하고자 하는 유저의 이름이 빈문자열일 수 없음");
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
        Validator.validateNotNull(newUserName, "변경 하려는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(newUserName, "변경 하려는 유저의 이름이 빈문자열일 수 없음");
        targetUser.setUserName(newUserName.trim());
        return targetUser;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        users.remove(id);
    }

    // 해당 channel Id를 가진 유저 목록을 반환
    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        return users.values()
                    .stream()
                    .filter(user ->
                            user.getChannels().
                                    stream().
                                    anyMatch(channel -> channel.getId().equals(channelId)))
                    .toList();
    }
}
