package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.io.*;


public class FileMessageService implements MessageService {
    private final File file = new File("messages.dat");
    private Map<UUID, Message> messageMap;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
        if(file.exists()) {
            load();
        } else {
            this.messageMap = new HashMap<>();
        }
    }

    // 역직렬화
    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.messageMap = (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("데이터 로드 중 오류가 발생" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 직렬화
    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.messageMap);
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Message createMessage(String content, UUID channelId, UUID userId){
        Channel channel = channelService.findChannelByChannelId(channelId);
        User user = userService.findUserById(userId);

        Message newMessage = new Message(content, channel, user);
        messageMap.put(newMessage.getId(), newMessage);

        user.addMessage(newMessage);
        channel.addMessage(newMessage);

        saveFile();
        return newMessage;
    }

    public List<Message> findMessagesByChannelId(UUID channelId){
        Channel channel = channelService.findChannelByChannelId(channelId);
        return channel.getChannelMessages();
    }

    public List<Message> findMessagesByUserId(UUID userId){
        User user = userService.findUserById(userId);

        return user.getMyMessages();
    }

    public List<Message> findAllMessages(){
        return new ArrayList<>(messageMap.values());
    }

    public Message findMessageById(UUID id){
        Message message = messageMap.get(id);
        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 없습니다.");
        }
        return message;
    }

    public Message updateMessage(UUID id, String newContent){
        Message targetMessage = findMessageById(id);

        targetMessage.updateContent(newContent);
        System.out.println("메시지가 수정되었습니다");

        saveFile();
        return targetMessage;
    }

    public void deleteMessage(UUID id){
        Message targetMessage = findMessageById(id);

        // 유저 쪽 리스트에서 삭제
        if (targetMessage.getUser() != null) {
            targetMessage.getUser().getMyMessages().remove(targetMessage);
        }
        // 채널 쪽 리스트에서 삭제
        if (targetMessage.getChannel() != null) {
            targetMessage.getChannel().getChannelMessages().remove(targetMessage);
        }

        messageMap.remove(id);
        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());

        saveFile();
    }

}
