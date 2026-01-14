package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ModelManager;
import com.sprint.mission.discodeit.service.RelationManager;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final ModelManager<User> userManager;
    private final RelationManager<User, Message> userMessageManager;
    private final RelationManager<User, Channel> userChannelManager;

    public JCFUserService() {
        this(new JCFModelManager<>(), new JCFRelationManager<>(), new JCFRelationManager<>());
    }

    public JCFUserService(ModelManager<User> userManager,
                          RelationManager<User, Message> userMessageManager,
                          RelationManager<User, Channel> userChannelManager) {
        this.userManager = userManager;
        this.userMessageManager = userMessageManager;
        this.userChannelManager = userChannelManager;
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
        userChannelManager.deleteKey(user.getUuid());
    }

    @Override
    public List<UUID> getMessageUUIDs(User user) {
        return List.copyOf(userMessageManager.read(user));
    }

    @Override
    public List<UUID> getChannelUUIDs(User user) {
        return List.copyOf(userChannelManager.read(user));
    }

    @Override
    public void deleteChannelFromUsers(Channel channel, List<UUID> userUUIDs) {
        userUUIDs.forEach(uuid -> userChannelManager.delete(uuid, channel.getUuid()));
    }

    @Override
    public void addChannel(User user, Channel channel) {
        userChannelManager.create(user, channel);
    }

    @Override
    public void deleteMessage(User user, Message message) {
        userMessageManager.delete(user.getUuid(), message.getUuid());
    }
}
