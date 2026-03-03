package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class FileChannelService implements ChannelService, ChannelRepository {

  private final List<Channel> data = new ArrayList<>();
  private final Path filePath;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  public FileChannelService(MessageRepository messageRepository,
      ReadStatusRepository readStatusRepository) {
    this.messageRepository = messageRepository;
    this.readStatusRepository = readStatusRepository;
    this.filePath = Path.of("data", "channels.ser");
    load();
  }

  private void load() {
    if (Files.notExists(filePath)) {
      return;
    }
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
      try (ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(filePath.toFile()))) {
        oos.writeObject(data);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ===== CREATE =====

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    data.add(channel);
    save();
    return channel;
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    data.add(channel);
    save();
    for (UUID userId : request.participantIds()) {
      ReadStatus readStatus = ReadStatus.create(userId, channel.getId());
      readStatusRepository.save(readStatus);
    }
    return channel;
  }

  // ===== FIND =====

  @Override
  public ChannelResponse findWithDetails(UUID channelId) {
    Channel channel = findChannel(channelId);  // 내부용 헬퍼
    return toResponse(channel);
  }

  @Override
  public ChannelResponse find(UUID channelId) {
    return toResponse(findChannel(channelId));
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<UUID> privateChannelIds = readStatusRepository.findAll().stream()
        .filter(rs -> rs.getUserId().equals(userId))
        .map(ReadStatus::getChannelId)
        .collect(Collectors.toList());

    return data.stream()
        .filter(channel ->
            channel.getType() == ChannelType.PUBLIC
                || privateChannelIds.contains(channel.getId()))
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Deprecated
  public List<Channel> findAll() {
    return new ArrayList<>(data);
  }

  // ===== UPDATE =====

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = findChannel(channelId);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    channel.update(request.newName(), request.newDescription());
    save();
    return channel;
  }

  // ===== DELETE =====

  @Override
  public void delete(UUID channelId) {
    Channel channel = findChannel(channelId);

    messageRepository.findAllByChannelId(channelId)
        .forEach(msg -> messageRepository.deleteById(msg.getId()));

    readStatusRepository.findAll().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .forEach(rs -> readStatusRepository.deleteById(rs.getId()));

    data.remove(channel);  // ✅ Channel 객체로 제거
    save();
  }

  // ===== ChannelRepository 구현 =====

  @Override
  public Channel save(Channel channel) {
    data.removeIf(c -> c.getId().equals(channel.getId()));
    data.add(channel);
    save();
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return data.stream().filter(c -> c.getId().equals(id)).findFirst();
  }

  @Override
  public boolean existsById(UUID id) {
    return findById(id).isPresent();
  }

  @Override
  public void deleteById(UUID id) {
    data.removeIf(c -> c.getId().equals(id));
    save();
  }

  // ===== 헬퍼 메서드 =====

  // Channel 엔티티를 직접 반환하는 내부용 메서드
  private Channel findChannel(UUID channelId) {
    return data.stream()
        .filter(c -> c.getId().equals(channelId))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
  }

  private ChannelResponse toResponse(Channel channel) {
    Instant lastMessageTime = getLastMessageTime(channel.getId());
    if (channel.getType() == ChannelType.PRIVATE) {
      List<UUID> participantUserIds = getParticipantUserIds(channel.getId());
      return ChannelResponse.from(channel, lastMessageTime, participantUserIds);
    }
    return ChannelResponse.from(channel, lastMessageTime);
  }

  private Instant getLastMessageTime(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }

  private List<UUID> getParticipantUserIds(UUID channelId) {
    return readStatusRepository.findAll().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .map(ReadStatus::getUserId)
        .collect(Collectors.toList());
  }
}
