package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            userMapper.toDto(message.getAuthor()), // 연관된 데이터를 추가 조회 -> N + 1 문제 발생
            message.getAttachments()
                .stream()
                .map(
                    binaryContentMapper::toDto
                ).toList()
        );
    }
}
