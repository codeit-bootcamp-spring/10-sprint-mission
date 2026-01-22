package com.sprint.mission.discodeit.view;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class BasicChannelMessageView {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 채널별 메시지 출력
    public static String viewMessage(Channel channel, MessageService messageService) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(channel.getChannelName()).append(" 채팅방]").append("\n");

        String messagesForChannel = messageService.findAllByChannelMessage(channel.getId()).stream()
                .map(BasicChannelMessageView::formatMessage)
                .collect(Collectors.joining("\n"));

        if (messagesForChannel.isEmpty()) sb.append("(메시지 없음)");
        else sb.append(messagesForChannel);

        return sb.toString();
    }

    // 서버 전체 메시지 출력
    public static String viewAllMessages(MessageService messageService) {
        String allMessages = messageService.findAllMessage().stream()
                .map(BasicChannelMessageView::formatMessage)
                .collect(Collectors.joining("\n"));
        return allMessages.isEmpty() ? "(메시지 없음)" : allMessages;
    }

    // 단건 메시지 출력
    public static String viewSingleMessage(Message message) {
        return formatMessage(message);
    }

    // 메시지 포맷팅 (작성자 : 내용 (타임스탬프))
    private static String formatMessage(Message message) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(message.getCreatedAt()),
                ZoneId.systemDefault()
        );
        return message.getSender().getUserName()
                + " : " + message.getContent()
                + " (" + dateTime.format(FORMATTER) + ")";
    }
}
