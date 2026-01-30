package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.entity.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createChannel(String channelName) {
        Validation.notBlank(channelName, "채널 이름");
        Validation.noDuplicate(
                channelRepository.findAll(),
                ch -> ch.getChannelName().equals(channelName),
                "이미 존재하는 채널명입니다: " + channelName
        );
        Channel channel = new Channel(channelName);
        channelRepository.save(channel);
        return channel;
    }
    @Override
    public List<Channel> getChannelAll() {
        return channelRepository.findAll();
    }


    @Override
    public Channel findChannelById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다: " + id));
    }

    @Override
    public Channel getChannelByName(String channelName) {
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다: " + channelName));
    }

    //
    @Override
    public void deleteChannel(UUID channelId) {
        // 없는 ID면 예외
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("삭제할 채널이 없습니다: " + channelId));
        // Participant 전부 퇴장
        List<User> participants = new ArrayList<>(channel.getParticipants());
        for(User u: participants){
            u.leaveChannel(channel);
            userRepository.save(u);
        }
        channelRepository.save(channel);

        //채널의 메세지들 전부 삭제 -> 하기전에 메세지랑 관련되 있는 유저들도 삭제해?야함
        List<Message> messages = messageRepository.findAll().stream()
                .filter(m->m.getChannelId().equals(channelId))
                .toList();
        for(Message m : messages){
            User sender = userRepository.findById(m.getSenderId())
                    .orElseThrow(() -> new NoSuchElementException("보낸 유저가 없습니다: " + m.getSenderId()));

            sender.getMessageIds().remove(m.getId());
            userRepository.save(sender);

            //repo에서 메세지 삭제
            messageRepository.delete(m.getId());

        }
        channelRepository.save(channel);
        channelRepository.delete(channelId);
    }

    @Override
    public Channel updateChannel(UUID uuid, String newName) {
        Validation.notBlank(newName, "채널 이름");

        Channel existing = channelRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("수정할 채널이 없습니다: " + uuid));

        // 중복 이름 검사(본인 채널명으로 바꾸는 경우는 허용)
        Validation.noDuplicate(
                channelRepository.findAll(),
                ch -> ch.getChannelName().equals(newName) && !ch.getId().equals(uuid),
                "이미 존재하는 채널명입니다: " + newName
        );
        existing.update(newName);
        channelRepository.save(existing);
        return existing;
    }


    @Override
    // ChatCoordinator 기능 구현
    public void joinChannel(UUID userId, UUID channelId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다 : " + userId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다 : " + channelId));

        user.joinChannel(channel); // 유저에도 동기화

        // 저장소 반영
        userRepository.save(user);
        channelRepository.save(channel);

    }
    @Override
    public void leaveChannel(UUID userId, UUID channelId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다 : " + userId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다 : " + channelId));

        user.leaveChannel(channel);
        userRepository.save(user);
        channelRepository.save(channel);
    }



}

