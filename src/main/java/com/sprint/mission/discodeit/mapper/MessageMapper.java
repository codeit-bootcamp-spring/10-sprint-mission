package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(source = "id", target = "messageId")
  @Mapping(source = "channel.id", target = "channelId")
  @Mapping(source = "author.id", target = "userId")
  @Mapping(source = "attachments", target = "attachmentIds")
  MessageResponse toResponse(Message message);

  default List<UUID> mapAttachmentIds(List<BinaryContent> attachments) {
    if (attachments == null) {
      return List.of();
    }
    return attachments.stream()
        .map(com.sprint.mission.discodeit.entity.BinaryContent::getId)
        .toList();
  }
}
