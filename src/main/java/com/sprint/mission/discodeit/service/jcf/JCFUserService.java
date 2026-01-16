package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFUserService implements UserService {
    final ArrayList<User> list;

    public JCFUserService() {
        this.list = new ArrayList<>();
    }

    @Override
    public User createUser(String userName, String userEmail) {
        Validators.validationUser(userName, userEmail);
        validateDuplicationEmail(userEmail);
        User user = new User(userName, userEmail);
        list.add(user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        return validateExistenceUser(id);
    }

    @Override
    public List<User> readAllUser() {
        return list;
    }

    @Override
    public User updateUser(UUID id, String userName, String userEmail) {
        User user = validateExistenceUser(id);
        Optional.ofNullable(userName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
            user.updateUserName(name);
        });
        Optional.ofNullable(userEmail)
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
            validateDuplicationEmail(email);
            user.updateUserEmail(email);
        });

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = validateExistenceUser(id);
        list.remove(user);
    }


    @Override
    public List<User> readUsersByChannel(UUID channelId) {
        return list.stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(ch -> channelId.equals(ch.getId())))
                .toList();
    }

    private void validateDuplicationEmail(String userEmail) {
        if(list.stream()
                .anyMatch(user -> userEmail.equals(user.getUserEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return list.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    }
}
