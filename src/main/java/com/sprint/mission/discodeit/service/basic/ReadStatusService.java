package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadStatusService {
    private ReadStatusRepository readStatusRepository;
    private ChannelRepository channelRepository;
    private UserRepository userRepository;

    public ReadStatusService(ReadStatusRepository readStatusRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.readStatusRepository = readStatusRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    public ReadStatusDTO create(ReadStatusCreateDTO readStatusCreateDTO) {
        // 관련된 유저, 채널이 존재하지 않으면 예외 발생
        User user = this.userRepository.loadById(readStatusCreateDTO.userId());
        Channel channel = this.channelRepository.loadById(readStatusCreateDTO.channelId());

        // 이미 관련된 유저와 채널이 있다면 예외 발생
        for (ReadStatus readStatusCheck : this.readStatusRepository.loadAll()) {
            if (readStatusCheck.getChannelId().equals(readStatusCreateDTO.channelId()) ||
                readStatusCheck.getUserId().equals(readStatusCreateDTO.userId())) {
                throw new NoSuchElementException();
            }
        }

        ReadStatus readStatus = new ReadStatus(channel.getId(), user.getId());
        return createReadStatusDTO(readStatus);
    }

    public ReadStatusDTO findById(UUID readStatusId) {
        ReadStatus readStatus = this.readStatusRepository.loadById(readStatusId);

        return createReadStatusDTO(readStatus);
    }

    public List<ReadStatusDTO> findAllByUserId(UUID userId) {
        List<ReadStatusDTO> readStatusDTOList = new ArrayList<>();

        for (ReadStatus readStatus : this.readStatusRepository.loadAll()) {
            if (readStatus.getUserId().equals(userId)) {
                readStatusDTOList.add(createReadStatusDTO(readStatus));
            }
        }

        return readStatusDTOList;
    }

    public ReadStatusDTO update(ReadStatusUpdateDTO readStatusUpdateDTO) {
        ReadStatus readStatus = this.readStatusRepository.loadById(readStatusUpdateDTO.readStatusId());
        readStatus.updateTime();

        return createReadStatusDTO(readStatus);
    }

    public void delete(UUID readStatusId) {
        this.readStatusRepository.delete(readStatusId);
    }

    public ReadStatusDTO createReadStatusDTO(ReadStatus readStatus) {
        return new ReadStatusDTO(
                readStatus.getId(),
                readStatus.getUpdatedAt()
        );
    }
}
