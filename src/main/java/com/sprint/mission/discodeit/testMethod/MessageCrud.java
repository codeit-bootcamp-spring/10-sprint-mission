package com.sprint.mission.discodeit.testMethod;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MessageCrud {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    // 메세지 작성
    public void sendMessage(String userName, String channelNameOrId, String content, String[][] filePathAndType) {
        User user = userService.checkUserByName(userName);
        Channel channel = channelService.checkChannel(channelNameOrId);
        List<BinaryContentDto.BinaryContentRequest> fileInfo = Arrays.stream(filePathAndType)
                .map(pathAndType -> new BinaryContentDto.BinaryContentRequest(pathAndType[0], pathAndType[1])).toList();
        messageService.create(new MessageDto.MessageCreateRequest(content, user.getId(), channel.getId(), fileInfo));
        System.out.println("채널에 메세지가 작성되었습니다.");
    }

    // 메세지 수정
    public void updateMessage(String channelNameOrId, String messageId, String content, String[][] filePathAndType) {
        Channel channel = channelService.checkChannel(channelNameOrId);
        UUID message = channel.getMessageIds().stream().filter(messageID -> messageID.toString().substring(0,8).equals(messageId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("채널에 존재하지 않는 메세지입니다."));
        List<BinaryContentDto.BinaryContentRequest> fileInfo = Arrays.stream(filePathAndType)
                .map(pathAndType -> new BinaryContentDto.BinaryContentRequest(pathAndType[0], pathAndType[1])).toList();
        messageService.update(message, new MessageDto.MessageUpdateRequest(content, fileInfo));
        System.out.println("메세지가 수정되었습니다.");
    }

    // 메세지 삭제
    public void deleteMessage(String channelNameOrId, String messageId) {
        Channel channel = channelService.checkChannel(channelNameOrId);
        UUID message = channel.getMessageIds().stream().filter(messageID -> messageID.toString().substring(0,8).equals(messageId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("채널에 존재하지 않는 메세지입니다."));
        messageService.delete(message);
        System.out.println("메세지가 삭제되었습니다.");
    }


}
