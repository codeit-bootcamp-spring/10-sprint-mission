package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public MessageResponseDto create(MessageCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
        // 메시지 객체 생성
        Message message = new Message(dto.getUserId(),dto.getText(),dto.getChannelId());
        List<BinaryContentCreateDto> binaryContentList = dto.getBinaryContentList();
        binaryContentList.forEach(bcDto -> {
                    BinaryContent binaryContent =  new BinaryContent(null,
                            message.getId(),
                            bcDto.getFileData(),
                            bcDto.getName(),
                            bcDto.getFileType());
                    message.addBinaryContent(binaryContent.getId());
                    binaryContentRepository.save(binaryContent);
                }

        );
        // 유저와 채널에 연관성 추가
        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        // 데이터에 정보 저장
        messageRepository.save(message);
        userRepository.save(user);
        channelRepository.save(channel);

        return messageMapper.toDto(message);
    }

    @Override
    public MessageResponseDto findMessage(UUID messageId) {
        Message message = getMessage(messageId);
        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageResponseDto> findMessageByKeyword(UUID channelId, String keyword) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        List<MessageResponseDto> messageList = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> message.getText().contains(keyword))
                .map(messageMapper::toDto)
                .toList();

        System.out.println(channel+"채널의 " + "[" + keyword + "]를 포함한 메시지 조회");
        messageList.forEach(System.out::println);

        return messageList;
    }

    @Override
    public List<MessageResponseDto> findAllMessagesByChannelId(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));

        List<MessageResponseDto> messageList = channel.getMessageList().stream()
                .map(messageId -> messageMapper.toDto(getMessage(messageId)))
                .toList();

        System.out.println("-- " + channel + "에 속한 메시지 조회 --");
        messageList.forEach(System.out::println);

        return messageList;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = getMessage(messageId);

        // 해당 메시지가 속했던 유저와 채널에서 메시지 정보 삭제
        User user  = userRepository.findById(message.getUserId())
                .orElseThrow(()-> new NoSuchElementException("해당 사용자가 없습니다."));
        user.getMessageList().remove(messageId);
        userRepository.save(user);

        // 채널에 속한 메시지 리스트에서 메시지 정보 삭제
        Channel channel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
        channel.getMessageList().remove(messageId);
        channelRepository.save(channel);

        // 메시지와 연관된 모든 binaryContent 삭제
        List<UUID> binaryContentList = message.getBinaryContentList();
        binaryContentList.forEach(binaryContentRepository::delete);


        //데이터에서 메시지 삭제
        messageRepository.delete(messageId);
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto dto) {
        Message message = getMessage(dto.getId());
        User user  = userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new NoSuchElementException("해당 유저가 없습니다."));

        // 권한 체크
        if(!dto.getUserId().equals(message.getUserId())){
            throw new IllegalStateException("수정할 권한이 없습니다");
        }
        // 메시지 업데이트
        message.updateMessage(dto.getText());
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }
    // 유효성 검사
    private Message getMessage(UUID messageId){
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

}
