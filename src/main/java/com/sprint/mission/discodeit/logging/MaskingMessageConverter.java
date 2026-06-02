package com.sprint.mission.discodeit.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.List;

public class MaskingMessageConverter extends MessageConverter {

    // 마스킹 할 정보
    private static final List<String> MASKING_KEYS = List.of("password", "accessToken", "refreshToken");

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();

        if (message == null || message.isEmpty()) {
            return message;
        }

        String maskedMessage = message;
        for (String key : MASKING_KEYS) {
            String regex = "(?i)(\"?%s\"?\\s*[:=]\\s*\"?)([^\"';, \\]]+)(\"?)";
            maskedMessage = maskedMessage.replaceAll(String.format(regex, key), "$1*******$3");
        }

        return maskedMessage;
    }
}