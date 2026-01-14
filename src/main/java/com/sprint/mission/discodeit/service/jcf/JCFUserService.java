package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.Exception.DuplicationEmailException;
import com.sprint.mission.discodeit.Exception.UserNotFoundException;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    @Override
    public User userCreate(String userName, String userEmail) {
        for (User user : users.values()){
            if (user.getUserEmail().equals(userEmail)){
                throw new DuplicationEmailException("이미 존재하는 이메일 입니다.");
            }
        }
        User user = new User(userName, userEmail);
        users.put(user.getUserId(), user);

        return user;
    }

    @Override
    public User userFind(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> userFindAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User userUpdate(UUID userId, String userName, String userEmail) {
        User user = users.get(userId);

        if (user == null) {
            throw new UserNotFoundException("해당되는 회원이 없습니다.");
        }

        user.update(userName, userEmail);

        return user;
    }

    @Override
    public void userDelete(UUID userId) {
        User user = users.get(userId);

        if (user == null) {
            throw new UserNotFoundException("해당되는 회원이 없습니다.");
        }

        users.remove(userId);
    }
}
