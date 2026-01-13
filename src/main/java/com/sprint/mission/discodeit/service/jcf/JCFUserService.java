package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    final ArrayList<User> list;

    public JCFUserService() {
        this.list = new ArrayList<>();
    }

    @Override
    public User createUser(String userName, String userEmail, String userPassword) {
        validateDuplicationEmail(userEmail);
        validationUser(userName, userEmail, userPassword);
        User user = new User(userName, userEmail, userPassword);
        list.add(user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (User user : list) {
            if(id.equals(user.getId())){
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> readAllUser() {
       return list;
    }

    @Override
    public void updateUser(UUID id, String userName, String userEmail, String userPassword) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        User user = validateExistence(id);
        validateDuplicationEmail(userEmail);
        validationUser(userName, userEmail, userPassword);
        user.updateUserName(userName);
        user.updateUserEmail(userEmail);
        user.updateUserPassword(userPassword);
    }

    public boolean isUserDeleted(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (User user : list) {
            if(id.equals(user.getId())) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void deleteUser(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        User user = validateExistence(id);
        list.remove(user);
    }

    private void validationUser(String userName, String userEmail, String userPassword) {
        Objects.requireNonNull(userName, "userName은 null이 될 수 없습니다.");
        if(userName.isBlank()) {
            throw new IllegalArgumentException("userName에 공백을 입력할 수 없습니다.");
        }
        Objects.requireNonNull(userEmail, "userEmail은 null이 될 수 없습니다.");
        if(userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail에 공백을 입력할 수 없습니다.");
        }
        Objects.requireNonNull(userPassword, "userPassword는 null이 될 수 없습니다.");
        if(userPassword.isBlank()) {
            throw new IllegalArgumentException("userPassword에 공백을 입력할 수 없습니다.");
        }
    }

    private void validateDuplicationEmail(String userEmail) {
        for (User user : list) {
            if (user.getUserEmail().equals(userEmail)) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }
        }
    }

    private User validateExistence(UUID id) {
        User user = readUser(id);
        if(user == null) {
            throw new NoSuchElementException("유저 id가 존재하지 않습니다.");
        }
        return user;
    }

}
