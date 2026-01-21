package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.io.*;


public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository = new FileMessageRepository();
    private final ChannelRepository channelRepository = new FileChannelRepository();
    private final UserRepository userRepository = new FileUserRepository();

    public Message createMessage(String content, UUID channelId, UUID userId){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Message newMessage = new Message(content, channel, user);

        user.addMessage(newMessage);
        channel.addMessage(newMessage);

        messageRepository.save(newMessage);
        userRepository.save(user);
        channelRepository.save(channel);

        return newMessage;
    }

    public List<Message> findMessagesByChannelId(UUID channelId){
        return messageRepository.findByChannelId(channelId);
    }

    public List<Message> findMessagesByUserId(UUID userId){
        return messageRepository.findByUserId(userId);
    }

    public List<Message> findAllMessages(){
        return messageRepository.findAll();
    }

    public Message findMessageById(UUID id){
        return messageRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));
    }

    public Message updateMessage(UUID id, String newContent){
        Message targetMessage = findMessageById(id);
        targetMessage.updateContent(newContent);

        messageRepository.save(targetMessage);

        System.out.println("메시지가 수정되었습니다");

        return targetMessage;
    }

    public void deleteMessage(UUID id){
        Message targetMessage = findMessageById(id);
        userRepository.findById(targetMessage.getUser().getId())
                      .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        messageRepository.delete(id);
        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());
    }

}
