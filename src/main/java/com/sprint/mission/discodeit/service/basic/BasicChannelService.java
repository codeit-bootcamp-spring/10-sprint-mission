package com.sprint.mission.discodeit.service.basic;
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
    @Override
    public void deleteChannel(UUID uuid) {
        // 없는 ID면 예외
        channelRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("삭제할 채널이 없습니다: " + uuid));
        channelRepository.delete(uuid);
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



}

