package com.sprint.mission.descodeit.service.file;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final String MESSAGE_FILE = "data/message.ser";
    private Map<UUID, Message> data;
    private UserService userService;
    private ChannelService channelService;

    public FileMessageService(){
        data = loadMessage();
    }
    @Override
    public void setDependencies(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message create(UUID userId, String text, UUID channelId) {
        data = loadMessage();
        User user = userService.findUser(userId);
        Channel channel = channelService.findChannel(channelId);
        // 메시지 객체 생성
        Message message = new Message(user, text, channel);
        // 데이터에 객체 추가
        data.put(message.getId(), message);
        saveMessage();

        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        data = loadMessage();
        Message message = Optional.ofNullable(data.get(messageId))
                .orElseThrow(()-> new NoSuchElementException("해당 메시지를 찾을 수 없습니다"));
        saveMessage();
        return message;
    }

    @Override
    public List<Message> findAllMessages() {
        data = loadMessage();
        System.out.println("[메세지 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));

        saveMessage();
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessageByKeyword(UUID channelId, String keyword) {
        data = loadMessage();
        Channel channel = channelService.findChannel(channelId);
        List<Message> messageList = data.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .filter(message -> message.getText().contains(keyword))
                .toList();
        System.out.println(channel+"채널의 " + "[" + keyword + "]를 포함한 메시지 조회");
        messageList.forEach(System.out::println);

        saveMessage();
        return messageList;
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.findChannel(channelId);

        System.out.println("-- " + channel + "에 속한 메시지 조회 --");
        List<Message> messageList = channel.getMessageList();
        messageList.forEach(System.out::println);

        saveMessage();
        return messageList;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findMessage(messageId);

        // 해당 메시지가 속했던 유저와 채널에서 메시지 정보 삭제
        User user = message.getUser();
        user.getMessageList().remove(message);

        Channel channel = message.getChannel();
        channel.getMessageList().remove(message);

        //데이터에서도 삭제
        data.remove(messageId);
        saveMessage();
    }

    @Override
    public Message update(UUID messageId,UUID requestUserId, String newText) {
        Message message = findMessage(messageId);
        if(!requestUserId.equals(message.getUser().getId())){
            throw new IllegalStateException("수정할 권한이 없습니다");
        }
        message.updateMessage(newText);
        System.out.println("수정완료!");
        saveMessage();
        return message;
    }

    public Map<UUID, Message> loadMessage(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MESSAGE_FILE))){
            return (Map<UUID,Message>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    public void saveMessage(){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(MESSAGE_FILE)))){
            oos.writeObject(this.data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
