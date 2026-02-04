//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//public class JCFChannelService implements ChannelService {
//    // 필드
//    private final List<Channel> channelData;
//    private MessageService messageService;
//    private UserService userService;
//    // 생성자
//    public JCFChannelService() {
//        // [저장]
//        this.channelData = new ArrayList<>();
//    }
//
//    // 생성
//    @Override
//    public Channel create(String name) {
//        // [비즈니스]
//        Channel channel = new Channel(name);
//        // [저장]
//        channelData.add(channel);
//        return channel;
//    }
//
//    // [비즈니스] 조회
//    @Override
//    public Channel find(UUID id) {
//        // [저장]
//        return channelData.stream()
//                .filter(channel -> channel.getId().equals(id))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
//    }
//
//    // [비즈니스] 전체 조회
//    @Override
//    public List<Channel> findAll(){
//        // [저장]
//        return channelData;
//    } // realALl이랑 read랑 type 맞춰줘야 하나?
//
//    // 수정
//    @Override
//    public Channel updateName(UUID channelID, String name) {
//        // [저장]
//        Channel channel = find(channelID);
//        // [비즈니스]
//        channel.updateName(name);
//        return channel;
//    }
//
//    // [비즈니스] Channel 자체 삭제
//    @Override
//    public void deleteChannel(UUID channelID) {
//        if (messageService == null) {
//            throw new IllegalStateException("MessageService is not set in JCFChannelService");
//        }
//        // [저장] 조회
//        Channel channel = find(channelID);
//
//        // [비즈니스] channel에 속한 user들 삭제
//        List<User> members = new ArrayList<>(channel.getMembersList());
//
//        // [비즈니스]
//        members.forEach(user -> user.leaveChannel(channel));
//
//        // [비즈니스] channel message 삭제 (Sender의 messageList, Channel messageList에서 삭제)
//        List<Message> messageList = new ArrayList<>(channel.getMessageList());
//        messageList.forEach(message -> messageService.deleteMessage(message.getId()));
//
//        // [저장] channelData에서 channel 삭제
//        channelData.remove(channel);
//    }
//
//    // channel에 user 가입
//    @Override
//    public void joinChannel (UUID userID, UUID channelID){
//        // Service 예외
//        if (userService == null) {
//            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
//        }
//        // [저장]
//        Channel channel = find(channelID);
//        User user = userService.find(userID);
//
//        if (channel.getMembersList().contains(user)) {
//            throw new IllegalArgumentException("User is already in this channel." + channelID);
//        }
//
//        // [비즈니스] channel에 user 추가
//        channel.addMember(user);
//
//        // [비즈니스] user에 가입한 channel 추가
//        user.joinChannel(channel);
//    }
//
//    @Override
//    public void leaveChannel (UUID userID, UUID channelID){
//        // Service 예외
//        if (userService == null) {
//            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
//        }
//        if (messageService == null) {
//            throw new IllegalStateException("MessageService is not set. Call setMessageService() before using create().");
//        }
//        // [저장]
//        Channel channel = find(channelID);
//        User user = userService.find(userID);
//
//        // 예외처리 : channel에 member 존재 X
//        if (!channel.getMembersList().contains(user)) {
//            throw new IllegalArgumentException("User is not in this channel." + channelID);
//        }
//
//        // [비즈니스] user에서 channel 삭제
//        user.leaveChannel(channel);
//
//        // [비즈니스]
//        // channel에서 user 삭제
//        channel.removeMember(user);
//
//        // [비즈니스] user가 보낸 messageList 중 해당 channel에 관한 것 삭제해줘야 함.
//        List<Message> messageList = new ArrayList<>(user.getMessageList());
//
//        // [비즈니스 + Message 저장] messageList에서 channel 과 일치하는 것을 delete
//        messageList.stream()
//                .filter(msg -> msg.getChannel().equals(channel))
//                .forEach(msg -> messageService.deleteMessage(msg.getId()));
//
//    }
//
//    // [비즈니스] Channel 안 모든 User 조회
//    @Override
//    public List<String> findMembers(UUID channelID){
//        // [저장]
//        Channel channel = find(channelID);
//        return channel.getMembersList().stream()
//                .map(User::getName)
//                .collect(Collectors.toList());
//    }
//}
