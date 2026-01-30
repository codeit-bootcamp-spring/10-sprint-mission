package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        Validation.notBlank(content, "메세지 내용");
        if (senderId == null || channelId == null) {
            throw new IllegalArgumentException("senderId나 channelId가 null일 수 없습니다.");
        }
        // ChatCoordinator 의 기능 추가/확장
        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 없습니다: " + senderId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 없습니다: " +channelId));

        Message message = new Message(content, senderId, channelId);
        messageRepository.save(message);

        user.addMessage(message.getId());
        channel.addMessages(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);

        return message;
    }

    // 단건 조회
    @Override
    public MessageResponse getMessageResponseByID(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("메세지가 없습니다: " + id));
        return assemble(message);
    }
    // 전체 조회
    @Override
    public List<MessageResponse> getAllMessagesResponse(){
        return messageRepository.findAll().stream()
                .map(this::assemble)
                .toList();
    }


    @Override
    public Message updateMessage(UUID uuid, String newContent) {
        Validation.notBlank(newContent, "메세지 내용");
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다: " + uuid));

        msg.update(newContent);
        messageRepository.save(msg);
        return msg;
    }
    @Override
    public void deleteMessage(UUID id){
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));
        UUID senderId = message.getSenderId();
        UUID channelId = message.getChannelId();

        // repo에서 메세지 삭제
        messageRepository.delete(id);
        //sender의 메세지 리스트에서 삭제.
        User sender = userRepository.findById(senderId)
                .orElseThrow(()-> new NoSuchElementException("보낸 유저가 없습니다: " + senderId));
        sender.getMessageIds().remove(id);
        userRepository.save(sender);

        //channel의 메세지 리스트에서 제거
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow((()-> new NoSuchElementException("해당 채널이 없습니다: "+ channelId)));
        channel.getMessageIds().remove(id);
        channelRepository.save(channel);
    }

    // chatCoordinator 기능 추가!!
    private MessageResponse assemble(Message message){
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new NoSuchElementException("보낸 유저가 없습니다: " + message.getSenderId()));

        Channel channel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("채널이 없습니다: " + message.getChannelId()));

        return new MessageResponse(
                message.getId(),
                message.getMessageContent(),
                message.getCreatedAt(),

                sender.getId(),
                sender.getUserName(),
                sender.getAlias(),

                channel.getId(),
                channel.getChannelName()
        );
    }


    // ChatCoordinator 가능 이식
    @Override
    public List<MessageResponse> getMessageResponsesBySenderId(UUID senderId) {
        return messageRepository.findAll().stream()
                .map(this::assemble)
                .toList();
    }
    @Override
    public List<MessageResponse> getMessageResponsesInChannel(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::assemble)
                .toList();
    }





}
