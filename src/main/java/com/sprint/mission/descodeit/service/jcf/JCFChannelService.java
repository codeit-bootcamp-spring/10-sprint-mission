package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;
    private final MessageService messageService;

    public JCFChannelService(UserService userService, MessageService messageService){
        this.data = new HashMap<>();
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel joinUsers(UUID channelID, UUID ... userID) {
        Channel channel = findChannel(channelID);

        // 유저 추가
        for(UUID id : userID){
            channel.addUsers(userService.findUser(id));
            userService.findUser(id).addChannel(channel);
        }
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelID) {
        Channel channel = Optional.ofNullable(data.get(channelID))
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        System.out.println("[채널 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        System.out.println();
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelID, String newName) {
        Channel channel = findChannel(channelID);
        channel.updateChannel(newName);
        return channel;
    }

    @Override
    public boolean delete(UUID channelID) {
        Channel channel = findChannel(channelID);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        for(User user : channel.getUserList()){
            userService.findUser(user.getId()).getChannelList().remove(channel);
        }
        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        for(Message message : new ArrayList<>(channel.getMessageList())){
            messageService.delete(message.getId());
        }
        data.remove(channelID);

        return true;
    }
}

