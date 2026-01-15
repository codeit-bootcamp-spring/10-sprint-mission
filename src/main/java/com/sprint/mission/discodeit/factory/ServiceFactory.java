package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

public class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private ServiceFactory() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService(userService);
        this.messageService =
                new JCFMessageService(userService, channelService);
    }

    public static ServiceFactory getInstance() {
        return instance;
    }
    public UserService userService() {
        return userService;
    }
    public ChannelService channelService() {
        return channelService;
    }
    public MessageService messageService() {
        return messageService;
    }
}
