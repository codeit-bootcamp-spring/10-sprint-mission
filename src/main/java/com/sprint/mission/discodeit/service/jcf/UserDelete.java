package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class UserDelete {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public UserDelete(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    public void userDelete(UUID userId){

        messageService.removeUser(userId);
        channelService.removeUserFromAllChannel(userId);
        userService.delete(userId);

    }

    public void channelDelete(UUID channelId){

        messageService.removeChannel(channelId); //Message클래스내 remove가 된거고.
        userService.removeChannel(channelId);
        channelService.delete(channelId);

    }
}
