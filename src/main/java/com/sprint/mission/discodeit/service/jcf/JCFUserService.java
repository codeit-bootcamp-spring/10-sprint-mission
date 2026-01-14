package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ModelManager;
import com.sprint.mission.discodeit.service.RelationManager;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
    private final ModelManager<User> userManager;
    private final RelationManager<User, Message> userMessageManager;

    public JCFUserService(ModelManager<User> userManager,
                          RelationManager<User, Message> userMessageManager) {
        this.userManager = userManager;
        this.userMessageManager = userMessageManager;
    }

    @Override
    public void createUser(User user) {
        userManager.create(user);
    }

    @Override
    public void sendMessage(User user, Message message) {
        userMessageManager.create(user, message);
    }

    @Override
    public void changeUser(User oldUser, User newUser) {
        userManager.update(oldUser.getUuid(), newUser.getUserId());
    }

    @Override
    public void deleteUser(User user) {
        userManager.delete(user.getUuid());
        userMessageManager.deleteKey(user.getUuid());
    }
}
