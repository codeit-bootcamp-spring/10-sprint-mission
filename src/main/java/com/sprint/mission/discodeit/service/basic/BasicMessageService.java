package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.FieldNotValidException;
import com.sprint.mission.discodeit.exception.InternalServiceException;
import com.sprint.mission.discodeit.exception.RequestNullException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final PageResponseMapper pageResponseMapper;

  @Transactional
  @Override
  public MessageDto create(List<MultipartFile> profiles, MessageCreateRequestDTO req) {
    if (req == null) {
      throw new RequestNullException();
    }

    // 메시지 생성 로그
    log.trace("메시지 생성 메서드 시작");

    // 요청에 담긴 채널 ID, 작성자 ID가 존재하는지 검증 시작
    // 로깅
    log.trace("메시지 생성 요청에 담긴 필드 검증 시작: channelId={}, authorId={}",
        req.channelId(), req.authorId());

    Channel channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(req.channelId()));
    User user = userRepository.findById(req.authorId())
        .orElseThrow(() -> new UserNotFoundException(req.authorId()));

    // 업로드 파일들을 BinaryContent로 변환하여 담아놓을 profileList를 빈 ArrayList로 초기화.
    List<BinaryContent> profileList = new ArrayList<>();

    // 업로드 파일이 존재하면 BinaryContent로 변환 및 profileList에 add
    if (profiles != null) {
      for (MultipartFile profile : profiles) {
        BinaryContent saved = binaryContentRepository.save(
            new BinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
            )
        );
        profileList.add(saved);

        try {

          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(
                  saved.id, profile.getBytes()
              )
          );
        } catch (IOException e) {
          log.error("IO 예외 발생!: {} ", e.getMessage(), e);
          throw new InternalServiceException();
        }
      }
    }

    Message message = new Message(req.content(), channel, user, profileList);
    log.debug("생성된 메시지 객체 정보: messageId={}, channelId={}, userId={}",
        message.getId(), message.getChannel().getId(), message.getAuthor().getId());
    Message saved = messageRepository.save(message);
    log.info("메시지 생성 및 영속화 성공: messageId={}", saved.getId());

    return messageMapper.toDto(saved);
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDto find(UUID messageId) {
    // messageId null 체크
    if (messageId == null) {
      throw new FieldNotValidException("messageId");
    }

    // 메시지 조회 메서드 시작 로그
    log.trace("메시지 조회 메서드 시작: messageId={}", messageId);

    // 메시지 레포지토리에서 messageId를 통해 메시지 추출
    Message message = getMessage(messageId);

    // 조회 성공 INFO 로그
    log.info("메시지 조회 성공: messageId={}", message.getId());

    // Dto로 변환 후 리턴
    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    // ChannelId로 메시지 리스트를 조회하는 메서드 시작 로그
    log.trace("채널 ID로 메시지 리스트 조회 메서드 시작: channelId={}", channelId);

    // channelId null 체크
    if (channelId == null) {
      throw new FieldNotValidException("channelId");
    }

    // 메시지 레포지토리에서 채널 ID에 해당하는 메시지들을 리스트로 추출
    List<Message> messages = messageRepository.findByChannelId(channelId);

    log.info("채널 내 메시지 리스트 조회 성공");

    // 리스트를 순회하면서 각 메시지들을 DTO로 변환 및 리스트화하여 리턴
    return messages
        .stream()
        .map(
            messageMapper::toDto
        ).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId,
      Optional<Instant> cursor,
      Pageable pageable) {

    // 채널 ID를 통해 모든 메시지를 조회하는 메서드 시작 로그
    log.trace("채널 내 모든 메시지 조회 메서드 시작: channelId={}", channelId);

    // 파라미터 null 체크
    if (channelId == null) {
      throw new FieldNotValidException("channelId");
    }
    if (pageable == null) {
      throw new FieldNotValidException("pageable");
    }

    // cursor 값과 pageable 정보를 통해 slice<message> 객체를 추출한다.
    Slice<Message> slice = cursor
        .map(value -> messageRepository.findByChannelIdAndCursor(channelId, value, pageable))
        .orElseGet(() -> messageRepository.findByChannelId(channelId, pageable));

    // slice 안의 메세지를 DTO로 전환
    Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);

    log.info("메시지 목록 조회 성공");
    return pageResponseMapper.fromSlice(dtoSlice);

  }

  @Transactional
  @Override
  // 인증된 사용자거나 메시지의 작성자일 때 수정이 가능하다.
  @PreAuthorize("isAuthenticated() and @messagePermissionEvaluator.isAuthor(#p0, principal.getUserDto().id())")
  public MessageDto update(UUID messageId, MessageUpdateRequestDto req) {
    // 파라미터 null 체크
    if (messageId == null) {
      throw new FieldNotValidException("messageId");
    }
    if (req == null) {
      throw new RequestNullException();
    }

    // 메시지 수정 메서드 시작 로그
    log.trace("메시지 수정 메서드 시작: messageId={}", messageId);

    // 수정할 메시지를 메시지 레포지토리에서 조회
    Message message = getMessage(messageId);

    // Target 메시지 정보 디버그 로그
    log.debug("target 메시지 정보: id={}, content={}",
        message.getId(), message.getContent());

    // 수정 요청에서 newContent 필드가 존재하면 기존 content를 newContent로 수정.
    if (req.newContent() != null) {
      message.setContent(req.newContent()); // dirty-checking
    }
    // 메시지 수정 성공 INFO 로그
    log.info("[Message] 메시지 수정 성공: messageId={}", message.getId());

    // DTO로 변환 후 리턴
    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  // 인증된 사용자거나 메시지 작성자만 해당 메시지 삭제 가능
  @PreAuthorize("isAuthenticated() and @messagePermissionEvaluator.isAuthor(#p0, principal.getUserDto().id())")
  public void delete(UUID messageId) {

    if (messageId == null) {
      throw new FieldNotValidException("messageId");
    }
    log.trace("메시지 삭제 메서드 시작: messageId={}", messageId);

    // 메시지 레포지토리에서 메시지 조회
    Message message = getMessage(messageId);
    // 디버깅 로그
    log.debug("삭제할 메시지 정보: id={}", message.getId());
    // 메시지 삭제
    messageRepository.delete(message);
    // 메시지 삭제 성공 INFO 로그
    log.info("메시지 삭제 성공: messageId={}", message.getId());
  }

  public Message getMessage(UUID id) {
    return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
  }
}

