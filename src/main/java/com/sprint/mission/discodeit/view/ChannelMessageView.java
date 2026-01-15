package com.sprint.mission.discodeit.view;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.exception.UserNotFoundException;

import com.sprint.mission.discodeit.service.UserService;

public class ChannelMessageView {
    public static String viewMessage(Channel channel, UserService userService) {

            StringBuilder sb = new StringBuilder();

            sb.append("[").append(channel.getChannelName()).append(" 채팅방]\n");

            for (Message message : channel.getMessages()) {
                try {
                    User sender = userService.findUser(message.getSenderId());

                    sb.append(sender.getUserName())
                            .append(": ")
                            .append(message.toString())
                            .append("\n");

                } catch (UserNotFoundException e) {
                    continue;
                }
            }

            return sb.toString();
    }

}
