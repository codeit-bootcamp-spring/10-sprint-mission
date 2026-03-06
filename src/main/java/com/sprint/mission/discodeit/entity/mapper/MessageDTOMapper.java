package com.sprint.mission.discodeit.entity.mapper;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageDTOMapper {

    public static MessageDto messageToResponseDTO(Message message, boolean online) {
        List<BinaryContentDto> attachments =
            message.getAttachments()
                .stream()
                .map(BinaryContentDTOMapper::binaryContentToResponse).toList();

        UserDto userDto = UserDTOMapper.userToResponse(message.getAuthor(), online);

        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            userDto,
            attachments
        );
    }
}
