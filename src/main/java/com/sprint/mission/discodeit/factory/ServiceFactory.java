package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class ServiceFactory {
    private static final UserService userService;
    private static final ChannelService channelService;
    private static final MessageService messageService;

    static {
        JCFUserService u = new JCFUserService();
        JCFChannelService c = new JCFChannelService(u);
        JCFMessageService m = new JCFMessageService(u, c);

        u.setChannelService(c);

        userService = u;
        channelService = c;
        messageService = m;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ChannelService getChannelService() {
        return channelService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }
}
