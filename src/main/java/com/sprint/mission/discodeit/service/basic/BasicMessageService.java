//package com.sprint.mission.discodeit.service.basic;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.ChannelUserRoleRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.util.*;
//
//public class BasicMessageService implements MessageService {
//    private final MessageRepository messageRepository;
//    private final UserRepository userRepository;
//    private final ChannelRepository channelRepository;
//    private final ChannelUserRoleRepository channelUserRoleRepository;
//
//    public BasicMessageService(MessageRepository messageRepository,
//                               UserRepository userRepository,
//                               ChannelRepository channelRepository,
//                               ChannelUserRoleRepository channelUserRoleRepository) {
//        this.messageRepository = messageRepository;
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.channelUserRoleRepository = channelUserRoleRepository;
//    }
//
//    @Override
//    public Message createMessage(String content, UUID userId, UUID channelId) {
//        User sender = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
//        Channel channel = channelRepository.findById(channelId)
//                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
//
//        // 권한 체크: Repository를 통해 직접 조회 (훨씬 효율적)
//        boolean isMember = channelUserRoleRepository.existsByChannelIdAndUserId(channelId, userId);
//        if (!isMember) {
//            throw new IllegalArgumentException("해당 채널에 참여하지 않은 유저는 메시지를 보낼 수 없습니다.");
//        }
//
//        Message message = new Message(content, sender, channel);
//
//        // 엔티티 간 관계 설정 (선택 사항이지만 객체 그래프 탐색을 위해 유지)
//        channel.addMessage(message);
//        // 주의: JPA와 달리 여기서는 channel 객체 변경이 DB에 자동 반영되지 않음.
//        // Repository 패턴에서는 channelRepository.save(channel)을 안 해도 message만 저장하면 충분하도록 설계하는 것이 보통입니다.
//        // 하지만 인메모리 특성상 양방향 관계 유지를 위해 channel도 업데이트 해주는 것이 좋을 수 있습니다.
//
//        return messageRepository.save(message);
//    }
//
//    @Override
//    public Message findMessageById(UUID messageId) {
//        return messageRepository.findById(messageId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
//    }
//
//    @Override
//    public List<Message> findAllMessagesByChannelId(UUID channelId) {
//        if (channelRepository.findById(channelId).isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
//        }
//        return messageRepository.findAllByChannelId(channelId);
//    }
//
//    @Override
//    public Message updateMessage(UUID messageId, String newContent) {
//        Message message = findMessageById(messageId);
//        message.updateContent(newContent);
//        return messageRepository.save(message);
//    }
//
//    @Override
//    public void deleteMessage(UUID messageId) {
//        // 필요하다면 채널에서 메시지 제거 로직 추가 (참조 무결성)
//        messageRepository.delete(messageId);
//        System.out.println("메시지 삭제 완료 (Basic).");
//    }
//
//    @Override
//    public void deleteAllMessagesByUserId(UUID userId) {
//        messageRepository.deleteAllByUserId(userId);
//    }
//
//    @Override
//    public void deleteAllMessagesByChannelId(UUID channelId) {
//        messageRepository.deleteAllByChannelId(channelId);
//    }
//}