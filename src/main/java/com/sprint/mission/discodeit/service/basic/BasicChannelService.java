package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.type.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.sprint.mission.discodeit.entity.type.ChannelType.PRIVATE;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    // 공용 채널
    public ChannelResponseDto createPublic(ChannelCreatePublicDto channelCreatePublicDto) {
        // 채널 생성
        Channel channel = new Channel(channelCreatePublicDto.getChannelName());
        channel.setChannelType(ChannelType.PUBLIC);
        // 채널 저장
        channelRepository.save(channel);
        return channelMapper.toPublicDto(channel);
    }
    // 개인 채널
    public ChannelResponseDto createPrivate(ChannelCreatePrivateDto channelCreatePrivateDto){
        // 채널 생성
        Channel channel = new Channel(channelCreatePrivateDto.getChannelName());
        // 입력으로 들어온 정해진 인원수 채널에 포함 및 유저 당 readStatus도 생성 후 저장
        channelCreatePrivateDto.getUserList().stream()
                        .map(userId -> userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다.")))
                        .forEach(user ->
                                {   channel.addUsers(user.getId());
                                    user.addChannel(channel.getId());
                                    userRepository.save(user);
                                    readStatusRepository.save(new ReadStatus(user.getId(), channel.getId()));
                                });


        channel.setChannelType(PRIVATE);
        channelRepository.save(channel);
        return channelMapper.toPrivateDto(channel);
    }

    @Override
    public ChannelResponseDto joinUsers(UUID channelId, UUID... userId) {
        Channel channel = getChannel(channelId);
        // 만약 채널이 프라이빗이라면 예외출력
        if(channel.getChannelType() == PRIVATE){
            throw new IllegalStateException("이 채널은 Private 채널입니다");
        }

        Arrays.stream(userId)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다")))
                .forEach(user -> {
                    channel.addUsers(user.getId());
                    user.addChannel(channelId);
                    userRepository.save(user);
                });
        channelRepository.save(channel);
        return channelMapper.toPublicDto(channel);
    }

    @Override
    public ChannelResponseDto findChannel(UUID channelId) {
        Channel channel = getChannel(channelId);
        if(channel.getChannelType() == ChannelType.PUBLIC){
            return channelMapper.toPublicDto(channel);
        }
        else{
            return channelMapper.toPrivateDto(channel);
        }
    }

    @Override
    public List<ChannelResponseDto> findAllChannelsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));

        System.out.println("[" + user + "가 속한 채널 조회]");

        // 공용채널 조회
        List<ChannelResponseDto> publicList = user.getChannelList().stream()
                .map(this::getChannel)
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC)
                .map(channelMapper::toPublicDto)
                .toList();
        // 개인채널 조회
        List<ChannelResponseDto> privateList = user.getChannelList().stream()
                .map(this::getChannel)
                .filter(channel -> channel.getChannelType() == ChannelType.PRIVATE)
                .map(channelMapper::toPrivateDto)
                .toList();

        // 반환할 리스트
        List<ChannelResponseDto> channelList = new ArrayList<>();
        // 합친 후 반환
        channelList.addAll(publicList);
        channelList.addAll(privateList);
        channelList.forEach(System.out::println);

        return channelList;
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto channelUpdateDto) {
        Channel channel = getChannel(channelUpdateDto.getId());
        if(channelUpdateDto.getChannelType() == ChannelType.PRIVATE){
            throw new IllegalStateException("Private 채널은 수정할 수 없습니다");
        }
        // 채널 업데이트
        channel.updateChannel(channelUpdateDto.getChannelName());
        // 채널 갱신
        channelRepository.save(channel);

        return channelMapper.toPublicDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = getChannel(channelId);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        List<UUID> userList = new ArrayList<>(channel.getUserList());
        userList.stream().map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다")))
                .forEach(user -> {
                    user.getChannelList().remove(channelId);
                    userRepository.save(user);
                });

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        List<UUID> messageList = new ArrayList<>(channel.getMessageList());
        messageList.stream().map(messageRepository::findById)
                .filter(Objects::nonNull).forEach(message ->{
                messageRepository.delete(message.getId());
                // 유저가 가지고 있던 메시지도 삭제
                User author = userRepository.findById(message.getUserId())
                        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
                if(author != null){
                    author.getMessageList().remove(message.getId());
                    // 정보 갱신
                    userRepository.save(author);
                }
                // 정보 갱신
                messageRepository.save(message);
        });

        // 채널과 연관된 ReadStatus도 삭제
        List<ReadStatus> readStatusList = readStatusRepository.findAll();
        readStatusList.stream()
                        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                        .forEach(readStatus -> readStatusRepository.delete(readStatus.getId()));

        channelRepository.delete(channelId);
    }

    private Channel getChannel(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));
    }
}
