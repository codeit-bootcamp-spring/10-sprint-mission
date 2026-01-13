package com.sprint.mission;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class Factory {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public Factory() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService(userService);
        this.messageService = new JCFMessageService(channelService, userService);
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
