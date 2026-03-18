package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatusdto.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequestDTO req) {
        Objects.requireNonNull(req.channelId(), "??ル쪇???? ??? id???낅퉵??");
        Objects.requireNonNull(req.userId(), "??ル쪇???? ??? id???낅퉵??");

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new NoSuchElementException("?????嶺??х몭???브퀡????? ???용????덈펲!"));
        User user = userRepository.findById(req.userId())
            .orElseThrow(() -> new NoSuchElementException("?????????띠럾? ?브퀡????? ???용????덈펲!"));

        try {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
            return readStatusMapper.toDto(readStatus);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("ReadStatus ??⑥щ턄??? 繞벿살탮???紐껊퉵??");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID rsId) {
        Objects.requireNonNull(rsId, "??ル쪇???? ??? ReadStatus ID ???낅퉵??");
        ReadStatus readStatus =
            readStatusRepository.findById(rsId)
                .orElseThrow(
                    () -> new NoSuchElementException("?????ReadStatus??嶺뚢돦堉??????怨몃쾳."));

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "??ル쪇???? ??? ??? ID ???낅퉵??");
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("????띠럾? ?브퀡????? ???곷쾳");
        }

        return readStatusRepository
            .findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto).toList();

    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequestDTO req) {
        Objects.requireNonNull(req, "??ル쪇???? ??? ??븐슙????낅퉵??");
        Objects.requireNonNull(id, "??ル쪇???? ??? ??紐끒???肉???덈펲!");

        ReadStatus readStatus = readStatusRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("?브퀡????? ???낅츎 ReadStatus ???낅퉵??")
        );

        readStatus.update();
        ReadStatus saved = readStatusRepository.save(readStatus);

        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            saved.getLastReadAt());

    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "??ル쪇???? ??? ID???낅퉵??");

        readStatusRepository.deleteById(id);

    }
}
