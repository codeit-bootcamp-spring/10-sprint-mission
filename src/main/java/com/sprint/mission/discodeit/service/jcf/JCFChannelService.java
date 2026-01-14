package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ModelManager;
import com.sprint.mission.discodeit.service.RelationManager;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ModelManager<Channel> channelManager;
    private final RelationManager<Channel, Message> channelMessageRelationManager;
    private final RelationManager<Channel, User> channelUserRelationManager;

    public JCFChannelService() {
        this(new JCFModelManager<>(), new JCFRelationManager<>(), new JCFRelationManager<>());
    }

    public JCFChannelService(ModelManager<Channel> channelManager,
                             RelationManager<Channel, Message> channelMessageRelationManager,
                             RelationManager<Channel, User> channelUserRelationManager) {
        this.channelManager = channelManager;
        this.channelMessageRelationManager = channelMessageRelationManager;
        this.channelUserRelationManager = channelUserRelationManager;
    }

    @Override
    public void createChannel(Channel channel) {
        channelManager.create(channel);
    }

    @Override
    public void addUser(Channel channel, User user) {
        channelUserRelationManager.create(channel, user);
    }

    @Override
    public void addMessage(Channel channel, Message message) {
        channelMessageRelationManager.create(channel, message);
    }

    @Override
    public void deleteUser(Channel channel, User user) {
        channelUserRelationManager.delete(channel.getUuid(), user.getUuid());
    }

    @Override
    public void changeChannelId(Channel oldChannel, Channel newChannel) {
        channelManager.update(oldChannel.getUuid(), newChannel.getChannelId());
    }

    @Override
    public List<UUID> getMessageUUIDs(Channel channel) {
        return channelMessageRelationManager.read(channel);
    }

    @Override
    public List<UUID> getUserUUIDs(Channel channel) {
        return channelUserRelationManager.read(channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        channelManager.delete(channel.getUuid());
        List<UUID> userUUIDs = getUserUUIDs(channel);
        userUUIDs.forEach(uuid -> channelUserRelationManager.delete(channel.getUuid(), uuid));
        List<UUID> messageUUIDs = getMessageUUIDs(channel);
        messageUUIDs.forEach(uuid -> channelMessageRelationManager.delete(channel.getUuid(), uuid));
    }

    @Override
    public List<Channel> getChannels() {
        return channelManager.readAll();
    }

    @Override
    public void deleteUserFromChannels(User user, List<UUID> channelUUIDs) {
        channelUUIDs.forEach(uuid -> channelUserRelationManager.delete(uuid, user.getUuid()));
    }

    @Override
    public void deleteMessage(Channel channel, Message message) {
        channelMessageRelationManager.delete(channel.getUuid(), message.getUuid());
    }
}
