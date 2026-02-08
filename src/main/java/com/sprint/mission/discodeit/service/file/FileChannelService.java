package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.status.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// ChannelService AND ChannelRepository 모두 구현하고, ChannelRepository 빈으로 등록
@Primary  // BasicMessageService에서 ChannelRepository 빈을 자동 주입할 수 있도록
@Service
public class FileChannelService implements ChannelService, ChannelRepository {

    private final List<Channel> data = new ArrayList<>();
    private final Path filePath;

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    public FileChannelService(MessageRepository messageRepository,ReadStatusRepository readStatusRepository){
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.filePath = Path.of("data","channels.ser");
        load();
    }


    private void load() {
        if (Files.notExists(filePath)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Channel> loaded = (List<Channel>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (InvalidClassException e) {
            System.out.println("이전 버전 파일을 무시하고 새로 시작합니다: " + e.getMessage());
            data.clear();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 고도화 1. create 메서드 분리

    public Channel createPrivateChannel(CreatePrivateChannelRequest request){
        // PRIVATE채널은 name,description 없이 생성
        Channel channel = new Channel(ChannelType.PRIVATE,null,null);
        data.add(channel);
        save();

        //참여자별 ReadStatus 생성
        for(UUID userId:request.getParticipantUserIds()){
            ReadStatus readStatus = ReadStatus.create(userId,channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    public Channel createPublicChannel(CreatePublicChannelRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC,request.getName(), request.getDescription());
        data.add(channel);
        save();
        return channel;
    }

    // ChannelService 인터페이스 메서드 (기존 메서드는 Deprecated, 하위 호환성)
    @Override
    @Deprecated
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        save();
        return channel;
    }

    // 고도화 2. find 메서드 분리

    // findWithDetails
    @Override
    public ChannelResponse findWithDetails(UUID channelId) {
        Channel channel = find(channelId);

        //최근 메시지 시간 조회
        Instant lastMessageTime = getLastMessageTime(channelId);

        //PRIVATE 채널인 경우 참여자 조회
        if(channel.getType() == ChannelType.PRIVATE){
            List<UUID> participantUserIds = getParticipantUserIds(channelId);
            return ChannelResponse.from(channel, lastMessageTime, participantUserIds);
        }

        //PUBLIC 채널
        return ChannelResponse.from(channel,lastMessageTime);

    }
    // findAllByUserId(사용자별 채널 조회)

    /* 특정 사용자가 볼 수 있는 채널 목록 조회
     * - Public 채널: 전체 조회
     * - Private 채널 : 해당 사용자가 참여한 채널만 조회
     */

    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // 사용자가 참여한 PRIVATE 채널 ID 목록
        List<UUID> privateChannelIds = readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toList());

        return data.stream()
                .filter(channel -> {
                    // PUBLIC 채널은 모두 포함
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }
                    // PRIVATE 채널은 참여한 것만 포함
                    return privateChannelIds.contains(channel.getId());
                })
                .map(channel -> {
                    Instant lastMessageTime = getLastMessageTime(channel.getId());

                    // PRIVATE 채널인 경우 참여자 정보 포함
                    if (channel.getType() == ChannelType.PRIVATE) {
                        List<UUID> participantUserIds = getParticipantUserIds(channel.getId());
                        return ChannelResponse.from(channel, lastMessageTime, participantUserIds);
                    }

                    return ChannelResponse.from(channel, lastMessageTime);
                })
                .collect(Collectors.toList());
    }

    // 기존 find, finadAll은 Deprecated
    @Override
    public Channel find(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
    }

    @Override
    @Deprecated
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    // 고도화 3. update (DTO 사용, PRIVATE 채널 수정 불가)
    /**
     * 채널 수정 - DTO 사용
     * PRIVATE 채널은 수정 불가
     */
    public ChannelResponse updateChannel(UpdateChannelRequest request) {
        Channel channel = find(request.getChannelId());

        //PRIVATE 채널 수정 불가
        if(channel.getType() == ChannelType.PRIVATE){
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        //PUBLIC 채널만 수정 가능
        Optional.ofNullable(request.getName()).ifPresent(channel::updateChannelName);
        Optional.ofNullable(request.getDescription()).ifPresent(channel::updateChannelDescription);
        channel.touch();
        save();

        //수정된 채널 정보를 DTO로 반환
        Instant lastMessageTime = getLastMessageTime(channel.getId());
        return ChannelResponse.from(channel, lastMessageTime);
    }

    // 기존 update 메서드 (하위 호환성)
    @Override
    @Deprecated
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = find(channelId);
        Optional.ofNullable(newName).ifPresent(channel::updateChannelName);
        Optional.ofNullable(newDescription).ifPresent(channel::updateChannelDescription);
        channel.touch();
        save();
        return channel;
    }

    // 고도화 4. delete (관련 도메인도 삭제)

    /**
     * 채널 삭제 - 관련 Message, ReadStatus도 함께 삭제
     */

    @Override
    public void delete(UUID channelId) {
        Channel channel = find(channelId);

        // 1. 해당 채널의 모든 메시지 삭제
        List<Message> channelMessages = messageRepository.findAll().stream()
                        .filter(msg -> msg.getChannelId().equals(channelId))
                        .collect(Collectors.toList());

        for(Message message : channelMessages) {
            messageRepository.deleteById(message.getId());
        }

        // 2. 해당 채널의 모든 ReadStatus 삭제
        List<ReadStatus> channelReadStatuses = readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .collect(Collectors.toList());

        for (ReadStatus readStatus : channelReadStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        // 3. 채널 삭제
        data.remove(channel);
        save();
    }

    // ChannelRepository 인터페이스 메서드
    @Override
    public Channel save(Channel channel) {
        // Channel이 이미 저장소에 있으면 update(이미 ChannelService에서 touch/save 처리),
        // 없으면 add 후 저장
        boolean exists = data.removeIf(c -> c.getId().equals(channel.getId()));
        if (!exists) {
            data.add(channel);
        } else {
            // 이미 존재하는 경우, touch()가 이미 업데이트 된 상태일 수 있으므로 바로 저장
        }
        save();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
        save();
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * 채널의 최근 메시지 시간 조회
     */
    private Instant getLastMessageTime(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null); // 메시지가 없으면 null
    }

    /**
     * PRIVATE 채널의 참여자 ID 목록 조회
     */
    private List<UUID> getParticipantUserIds(UUID channelId) {
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .collect(Collectors.toList());
    }
}
