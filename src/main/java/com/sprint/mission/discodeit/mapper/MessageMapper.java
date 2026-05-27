package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MessageMapper {

  @Autowired
  protected UserMapper userMapper;
  @Autowired
  protected BinaryContentMapper binaryContentMapper;

  public MessageDto toDto(Message message, Set<UUID> onlineUserIds) {
    return new MessageDto(
        message.getId(),
        message.getContent(),
        message.getChannel().getId(),
        userMapper.toDto(message.getAuthor(), onlineUserIds.contains(message.getAuthor().getId())),
        message.getAttachments().stream()
            .map(a -> binaryContentMapper.toDto(a))
            .collect(Collectors.toList()),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }

  //목록 조회용 디티오 매퍼
//인자로 받은 컨텐츠목록을 디티오 필드로 매핑
  public MessageDto toDto(Message message, List<BinaryContent> attachments,
      Set<UUID> onlineUserIds) {
    return new MessageDto(
        message.getId(),
        message.getContent(),
        message.getChannel().getId(),
        userMapper.toDto(message.getAuthor(), onlineUserIds.contains(message.getAuthor().getId())),
        attachments.stream()
            .map(a -> binaryContentMapper.toDto(a))
            .collect(Collectors.toList()),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }

  public Message toEntity(MessageCreateRequest dto, User user, Channel channel,
      List<BinaryContent> attachments) {
    return Message.create(dto.content(), channel, user, attachments);
  }

}
