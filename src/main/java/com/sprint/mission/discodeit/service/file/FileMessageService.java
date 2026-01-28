package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService,ChannelService channelService){
        this.data = load();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId){
        load();
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!user.getChannelList().contains(channel)) {
            throw new IllegalArgumentException(user.getUserName() + "유저가 "+  channel.getChannelName() + "채널에 없습니다.");
        }

        Message message = new Message(content,user,channel);
        user.getMessageList().add(message);
        userService.save();
        channel.getMessageList().add(message);
        channelService.save();
        data.put(message.getId(),message);

        save();

        return message;

    }

    @Override
    public Message findById(UUID id){
        load();
        if(data.get(id) == null){
            throw  new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
        return data.get(id);
    }

    //특정 User의 모든 메세지 목록
    @Override
    public List<Message> findAll() {
        load();
        return new ArrayList<>(data.values());
    }


    public List<Message> findByUser(UUID userId){
        User user = userService.findById(userId);
        return user.getMessageList();
    }

    @Override
    public Message update(UUID id,String content){
        load();
        Message message = findById(id);
        message.setContent(content);
        save();
        return message;
    }

    @Override
    public Message delete(UUID id) {
        load();
        Message message = findById(id);
        message.getUser().getMessageList().remove(message);
        userService.save();
        data.remove(id);
        save();
        return message;
    }

    public void removeUser(UUID userId){
        load();
        if(userId == null){
            throw new IllegalArgumentException("삭제하려는 유저가 없습니다.");
        }
        //삭제된 유저와 같은 유저id를 갖고있는 메시지를 지운다.
        data.values().removeIf(Message -> Message.getUser().getId().equals(userId));
        save();

    }

    public void removeChannel(UUID channelId){
        load();
        if(channelId == null){
            throw new IllegalArgumentException("삭제하려는 채널이 없습니다.");
        }
        //삭제된 체널과 같은 채널id를 갖고있는 메시지를 지운다.
        data.values().removeIf(Message -> Message.getChannel().getId().equals(channelId));
        save();
    }

    public void save(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("message.ser"))){
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<UUID, Message> load(){
        File file = new File("message.ser");

        //파일이 없을때 error 방지
        if (!file.exists()) {

            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("message.ser"))){

            return (Map<UUID, Message>) ois.readObject();

        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
