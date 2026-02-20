package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public ChannelDto.Response createPublic(ChannelDto.CreatePublic createRequest) {
    Channel channel = new Channel(
        ChannelType.PUBLIC,
        createRequest.name(),
        createRequest.description()
    );
    channelRepository.save(channel);

    return ChannelDto.Response.of(channel, List.of(), null);
  }

  @Override
  public ChannelDto.Response createPrivate(ChannelDto.CreatePrivate createRequest) {
    //채널 생성 후 저장
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);
    //채널 참여자 아이디 목록
    List<UUID> memberIds = createRequest.participantIds();
    //null 체크
    if (memberIds == null) {
      memberIds = List.of();
    }
    //User별 ReadStatus정보 생성
    for (UUID memberId : memberIds) {
      ReadStatus status = new ReadStatus(memberId, channel.getId());
      readStatusRepository.save(status);
    }
    return ChannelDto.Response.of(channel, memberIds, null);
  }

  @Override
  public ChannelDto.Response findById(UUID channelId) {
    //채널Id로 채널 객체 조회
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    return ChannelDto.Response.of(
        channel,
        getParticipantIds(channelId),
        getLastMessageAt(channelId));
  }

  @Override
  public List<ChannelDto.Response> findAllByUserId(UUID userId) {
    //public 채널 리스트
    List<ChannelDto.Response> publicChannels = channelRepository.findAll().stream()
        .filter(channel -> channel.getType() == ChannelType.PUBLIC)
        .map(channel -> ChannelDto.Response.of(channel, List.of(),
            getLastMessageAt(channel.getId())))
        .toList();
    //private 채널 리스트(내가 참여하고 있어야함)
    List<ChannelDto.Response> privateChannels = readStatusRepository.findAllByUserId(userId)
        .stream()
        .map(ReadStatus::getChannelId)
        .map(channelRepository::findById)
        .flatMap(Optional::stream)
        .filter(channel -> channel.getType() == ChannelType.PRIVATE)
        .map(channel -> ChannelDto.Response.of(
            channel,
            getParticipantIds(channel.getId()),
            getLastMessageAt(channel.getId()))
        )
        .toList();
    List<ChannelDto.Response> allChannels = new ArrayList<>();
    allChannels.addAll(publicChannels);
    allChannels.addAll(privateChannels);
    return allChannels;
  }

  @Override
  public ChannelDto.Response update(UUID channelId, ChannelDto.Update request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.CANNOT_UPDATE_PRIVATE_CHANNEL);
    }
    Optional.ofNullable(request.newName()).ifPresent(channel::updateChannelName);
    Optional.ofNullable(request.newDescription()).ifPresent(channel::updateDescription);

    channelRepository.save(channel);
    return findById(channel.getId());
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    //채널에 있는 메시지 목록
    List<Message> messages = messageRepository.findAll().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
    //메시지 속 첨부파일 삭제, 메시지 삭제
    for (Message message : messages) {
      for (UUID attachmentId : message.getAttachmentIds()) {
        binaryContentRepository.findById(attachmentId)
            .ifPresent(binaryContentRepository::delete);
      }
      messageRepository.delete(message);
    }

    //채널 삭제 시 ReadStatus 삭제
    readStatusRepository.findAllByChannelId(channelId)
        .forEach(readStatusRepository::delete);

    channelRepository.delete(channel);
  }

  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .map(BaseEntity::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }

  private List<UUID> getParticipantIds(UUID channelId) {
    return readStatusRepository.findAllByChannelId(channelId).stream()
        .map(ReadStatus::getUserId)
        .toList();
  }
}
