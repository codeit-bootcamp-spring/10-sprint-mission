package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

@Service
@AllArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public Channel createPrivateChannel(ChannelRequestDto channelCreateDto) { //type Private 일때

        //User 정보를 받아서 User별 ReadStatus 정보를 생성
        User user = userRepository.findById(channelCreateDto.userDto().userId()).orElseThrow(() -> new NoSuchElementException("유저가 없음"));
        Channel channel = channelRepository.save(new Channel(PRIVATE, null,null));
        ReadStatus readStatus = new ReadStatus(user.getId(),channel.getId());
        readStatusRepository.save(readStatus);

        return channel;

    }
    @Override
    public Channel createPublicChannel(ChannelRequestDto channelCreateDto) { //type PUBLIC 일때

        Channel channel = channelRepository.save(new Channel(PUBLIC, channelCreateDto.name(), channelCreateDto.discription()));
        return channel;

    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
    }
}
