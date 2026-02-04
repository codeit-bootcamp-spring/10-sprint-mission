package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.MessageCodesResolver;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    @Override
    public ChannelDto.Response create(ChannelDto.CreateRequest request) {
        // PUBLIC 채널 생성 시 기존 로직 유지, PRIVATE 채널 생성 시 생략
        String name = (request.type() == ChannelType.PUBLIC) ? request.name() : null;
        String description = (request.type() == ChannelType.PUBLIC) ? request.description() : null;

        Channel channel = new Channel(request.type(), name, description);
        channelRepository.save(channel);

        return convertToResponse(channel, null, null); // 초기 생성 시 최근 메시지, 유저 목록 없음
    }


    @Override
    public ChannelDto.Response find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        // 해당 채널의 가장 최근 메시지의 시간 정보
        Instant lastTime = messageRepository.findLastMessageOfChannel(channelId)
                .map(Message::getCreatedAt).orElse(null);

        // PRIVATE 채널의 경우 참여한 유저의 id 포함
        List<UUID> memberList = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            memberList = readStatusRepository.findAllByChannelId(channelId).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return convertToResponse(channel, lastTime, memberList);

    }

        @Override
        public List<ChannelDto.Response> findAllByUserId (UUID userId){
            // PUBLIC 채널: 전체 조회
            List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

            // PRIVATE: 유저가 참여한 채널만 조회
            List<Channel> myPrivateChannels = readStatusRepository.findAllByUserId(userId).stream()
                    .map(readStatus -> channelRepository.findById(readStatus.getChannelId()).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                    .toList();

            // 참여한 PUBLIC과 PRIVATE 채널 합치기: 특정 유저가 볼 수 있는 채널 목록 조회 가능
            List<Channel> myAvailableChannelList = new ArrayList<>();
            myAvailableChannelList.addAll(publicChannels);
            myAvailableChannelList.addAll(myPrivateChannels);

            // find() 재사용: 최근 메시지 시간 정보, PRIVATE 채널의 유저 Id 정보 포함
            return myAvailableChannelList.stream()
                    .map(channel -> find(channel.getId()))
                    .toList();
        }

        @Override
        public ChannelDto.Response update (ChannelDto.UpdateRequest request) {
            Channel channel = channelRepository.findById(request.channelId())
                    .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

            // PRIVATE 채널은 수정 불가
            if (channel.getType() == ChannelType.PRIVATE) {
                throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
            }

            channel.setName(request.newName());
            channel.setDescription(request.newDescription());

            channelRepository.save(channel);
            return convertToResponse(channel, null, null);
        }

        @Override
        public void delete (UUID channelId) {
            Channel channel = channelRepository.findById(channelId).orElseThrow();

            // 관련 도메인(Message, ReadStatus) 삭제
            messageRepository.deleteAllByChannelId(channelId);
            readStatusRepository.deleteAllByChannelId(channelId);

            channelRepository.deleteById(channelId);
        }

        private ChannelDto.Response convertToResponse (Channel channel, Instant lastMessageTime, List <UUID> memberId) {
            return ChannelDto.Response.builder()
                    .id(channel.getId())
                    .name(channel.getName())
                    .lastMessageAt(lastMessageTime)
                    .memberIdList(memberId)
                    .build();
        }
    }




