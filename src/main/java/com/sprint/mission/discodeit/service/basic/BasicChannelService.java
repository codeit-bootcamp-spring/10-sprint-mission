package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.mapper.ChannelDTOMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelDTOMapper channelDTOMapper;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    // 최신 메시지를 받아와서 Message로 리턴하는 메소드
    private Message getLatestMessage(UUID channelId){
        return messageRepository.findByChannelId(channelId)
                .stream()
                .max(Comparator.comparing(Message::getUpdatedAt))
                .orElse(null);
    }

    // Public 채널을 만드는 메소드
    // 요청 DTO에서 필드를 뽑아와 채널 생성
    // Public도 Private 처럼 유저 목록을 받아야하는지??
    @Override
    public ChannelResponseDTO createPublicChannel(PublicChannelCreateDTO req) {
        Objects.requireNonNull(req.name(), "유효하지 않은 채널 이름입니다.");
        Objects.requireNonNull(req.description(), "유효하지 않은 채널 설명입니다.");

        // 채널 리스트에 중복되는 이름이 있으면 예외 던짐.
        if(channelRepository.findAll()
                .stream()
                .anyMatch(c -> req.name().equals(c.getName())))
        {
            throw new IllegalStateException("해당 채널의 이름이 중복됩니다.");
        }

        // DTO 매퍼를 사용하여 DTO -> entity 동작
        // 생성자로 만들어진 인스턴스를 채널 레포지토리에서 영속화
        Channel channel = channelDTOMapper.publicReqToChannel(req);
        Channel saved = channelRepository.save(channel);

        // 최종적으로 만들어진 채널을 사용하여 채널 응답 DTO를 리턴
        // 새로 만들어진 채널 -> 메시지 존재하지 않음 -> 메시지 시간 정보 null 처리
        return channelDTOMapper.channelToResponseDTO(saved, getLatestMessage(saved.getId()));
    }


    // Private 채널을 만드는 메소드
    // 이름과 설명은 생략 -> null 처리?
    @Override
    public ChannelResponseDTO createPrivateChannel(PrivateChannelCreateDTO req) {
        Objects.requireNonNull(req.users(), "유효하지 않은 유저 목록 요청입니다.");

        if(req.users().isEmpty()){
            throw new IllegalStateException("유저 목록이 비어있습니다.");
        }

        // 이름과 설명 생략 -> null 처리 -> 영속화
        Channel channel = channelDTOMapper.privateReqToChannel(req);
        Channel saved = channelRepository.save(channel);

        // 요청 DTO의 유저 목록을 스트림
        req.users().forEach(u -> {
            ReadStatus rs = new ReadStatus(u, channel.getId()); // 각 유저별 readStatus 생성.
            readStatusRepository.save(rs); // userStatus 영속화
        });

        // 최종적으로 만들어진 channel을 사용하여 채널 응답 DTO를 리턴
        // 새로 만든 채널 -> 메시지가 없음 -> 메시지 시간 정보들을 null 처리
        return channelDTOMapper.channelToResponseDTO(saved, getLatestMessage(saved.getId()));
    }


    // 채널 ID를 매개변수로 받고, ChannelViewDTO를 만들어서 정보를 공개하는 메소드
    // ChannelViewDTO는 가장 최근 메세지의 시간 정보(createdAt, updatedAT)과
    // ChannelType 여부에 따라 User 정보를 담는다.
    @Override
    public ChannelResponseDTO find(UUID channelId) {

        // 채널 ID가 존재하는지 검증
        Channel channel = channelRepository
            .findById(channelId)
            .orElseThrow(() -> new IllegalStateException("해당 채널 ID가 존재하지 않습니다."));

        // 메세지 리포지토리에서 channelID를 통해 메세지 리스트를 뽑고 stream ->
        // max를 사용하여 가장 큰(최근) createdAt을 가진 메시지를 뽑고
        // 메시지가 존재하지 않으면 null 리턴? <- 추후 바꿔야할듯

        // 만약 채널 타입이 PRIVATE이면...
        if(channel.getType() == ChannelType.PRIVATE){
            // ChannelResponseDTO 생성 및 반환
            return channelDTOMapper.channelToResponseDTO(channel, getLatestMessage(channelId));
        }

        // 채널 타입이 Public이라면...
        return channelDTOMapper.channelToResponseDTO(channel, getLatestMessage(channelId));

    }


    // UserID를 통해 특정 유저가 볼 수 있는 Channel 목록을 조회하도록 하는 메소드
    // Public 채널은 모든 유저에게 공개 가능
    // Private 채널은 해당 유저가 가입되어 있을때만 공개 가능
    @Override
    public List<ChannelResponseDTO> findAllByUserId(UUID userID) {
        Objects.requireNonNull(userID,"유효하지 않은 유저ID 입니다.");

        // 채널 레포지토리에서 모든 채널들을 스트림
        return channelRepository.findAll().stream()
                // filter로 채널 타입이 Public이거나 채널 타입이 Private이면서 채널의 유저 리스트에 userID가 있는 채널들을 뽑아냄
               .filter(c -> c.getType() == ChannelType.PUBLIC
               || (c.getType() == ChannelType.PRIVATE && c.getUserList().contains(userID)))

               .map(
                       c -> {
                           // 메시지가 없으면 시간 정보들은 다 null 처리,
                           // 채널 타입이 Private이면 유저 정보를 DTO에 주입, Public이면 빈 리스트를 DTO에 주입
                           return channelDTOMapper.channelToResponseDTO(c,getLatestMessage(c.getId()));
                       }
               ).toList(); // 리스트화 하여 반환함.
    }



    // 채널이 유저를 가입시키는 메소드
    @Override
    public ChannelResponseDTO join(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.userJoin(userId);
        channelRepository.save(channel);
        Message latestMessage = getLatestMessage(channelId);

        return channelDTOMapper.channelToResponseDTO(channel, latestMessage);
    }

    @Override
    public ChannelResponseDTO leave(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.userLeave(userId);
        Channel saved = channelRepository.save(channel);

        return channelDTOMapper.channelToResponseDTO(saved, getLatestMessage(saved.getId()));
    }

    // 업데이트 요청 DTO를 매개변수로 받아 채널을 업데이트하는 서비스 메소드
    @Override
    public ChannelResponseDTO update(ChannelUpdateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        Objects.requireNonNull(req.channelID(), "유효하지 않은 채널입니다.");
        Objects.requireNonNull(req.channelID(), "유효하지 않은 이름입니다.");
        Objects.requireNonNull(req.channelID(), "유효하지 않은 설명입니다.");

        // 채널 레포지토리에서 req의 채널ID를 통하여 채널 찾음.
        Channel channel = channelRepository.findById(req.channelID())
                .orElseThrow(() -> new IllegalStateException("해당 채널 존재하지 않음."));

        // Private 채널은 수정이 불가하므로 예외 던짐.
        if(channel.getType() == ChannelType.PRIVATE){
            throw new IllegalStateException("개인 채널은 업데이트가 불가능합니다.");
        }

        // DTO 내부의 값들을 사용하여 채널 업데이트
        channel.update(req.newName(), req.newDescription());

        // 업데이트한 채널을 영속화
        Channel saved = channelRepository.save(channel);

        // 영속화된 채널 객체를 응답 DTO로 변환 및 리턴
        return channelDTOMapper.channelToResponseDTO(saved, getLatestMessage(saved.getId()));
    }

    // 채널 ID를 매개변수로 받아 해당 채널을 삭제하는 메소드
    @Override
    public void delete(UUID channelId) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널ID입니다.");

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        // 해당 채널에 존재하는 메시지의 ID를 사용
        // 메시지 레포지토리의 DeleteById를 통해 삭제한다.
        messageRepository.findByChannelId(channelId)
                .forEach(m -> messageRepository.deleteById(m.getId()));


        // readStatus 레포지토리의 모든 객체중에서
        // channelId를 가리키는 객체를 filter로 뽑아내고
        // forEach와 deleteByID 메소드를 통해 각각 제거.
        readStatusRepository.findAll()
                .stream()
                .filter(r -> channelId.equals(r.getChannelID()))
                .forEach(r -> readStatusRepository.deleteByID(r.getId()));

        // 목표 채널을 삭제 -> 영속화는 해당 메소드 내부에서 진행.
        channelRepository.deleteById(channelId);
    }
}
