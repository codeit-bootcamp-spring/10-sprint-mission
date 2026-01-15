package com.sprint.mission.discodeit.view;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.stream.Collectors;

public class ChannelMessageView {

    // 채널별 메시지 출력
    public static String viewMessage(Channel channel, JCFMessageService messageService) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(channel.getChannelName()).append(" 채팅방]").append("\n");

        String messagesForChannel = messageService.findAllByChannelMessage(channel).stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n"));

        if (messagesForChannel.isEmpty()) sb.append("(메시지 없음)");
        else sb.append(messagesForChannel);

        return sb.toString();
    }

    // 서버 전체 메시지 출력
    public static String viewAllMessages(JCFMessageService messageService) {
        String allMessages = messageService.findAllMessage().stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n"));
        return allMessages.isEmpty() ? "(메시지 없음)" : allMessages;
    }
}
