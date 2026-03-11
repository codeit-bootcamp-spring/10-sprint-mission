package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    // 硫붿꽭吏 ?앹꽦 硫붿냼??
    // ?좏깮?곸쑝濡?泥⑤? ?뚯씪(BinaryContent)瑜??щ윭 媛??깅줉?????덈떎.
    @Transactional
    @Override
    public MessageDto create(List<MultipartFile> profiles, MessageCreateRequestDTO req) {
        Objects.requireNonNull(req, "?좏슚?섏? ?딆? ?붿껌?낅땲??");
        Objects.requireNonNull(req.channelId(), "?좏슚?섏? ?딆? 梨꾨꼸ID ?낅땲??");
        Objects.requireNonNull(req.authorId(), "?좏슚?섏? ?딆? ?ъ슜?륤D ?낅땲??");

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new NoSuchElementException("?대떦 梨꾨꼸??議댁옱?섏? ?딆뒿?덈떎!"));
        User user = userRepository.findById(req.authorId())
            .orElseThrow(() -> new NoSuchElementException("?대떦 ?좎?媛 議댁옱?섏? ?딆뒿?덈떎!"));

        List<BinaryContent> profileList = new ArrayList<>();

        if (profiles != null) {
            for (MultipartFile profile : profiles) {
                BinaryContent saved = binaryContentRepository.save(
                    new BinaryContent(
                        profile.getName(),
                        profile.getSize(),
                        profile.getContentType()
                    )
                );
                profileList.add(saved);

                try {
                    binaryContentStorage.put(saved.getId(), profile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Byte ????ㅽ뙣");
                }

            }
        }

        Message message = new Message(req.content(), channel, user, profileList);
        Message saved = messageRepository.save(message);

        return messageMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        Objects.requireNonNull(messageId, "?좏슚?섏? ?딆? 硫붿떆吏ID ?낅땲??");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new IllegalStateException("議댁옱?섏? ?딅뒗 硫붿떆吏?낅땲??")
            );

        return messageMapper.toDto(message);


    }

    @Transactional
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "?좏슚?섏? ?딆? 梨꾨꼸id ?낅땲??");

        List<Message> messages = messageRepository.findByChannelId(channelId);

        return messages
            .stream()
            .map(
                messageMapper::toDto
            ).toList();

    }

//    @Override
//    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
//        Pageable pageable) {
//        return null;
//    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId,
        Optional<Instant> cursor,
        Pageable pageable) {
        Objects.requireNonNull(channelId, "?좏슚?섏? ?딆? 梨꾨꼸 ?앸퀎??");
        Objects.requireNonNull(cursor, "?좏슚?섏? ?딆? cursor!");
        Objects.requireNonNull(pageable, "?좏슚?섏? ?딆? ?섏씠吏??뺣낫!");

        Slice<Message> slice = cursor
            .map(value -> messageRepository.findByChannelIdAndCursor(channelId, value, pageable))
            .orElseGet(() -> messageRepository.findByChannelId(channelId, pageable));
        Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);

        return pageResponseMapper.fromSlice(dtoSlice);

    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequestDto req) {
        Objects.requireNonNull(req, "?좏슚?섏? ?딆? ?붿껌?낅땲??");
        Objects.requireNonNull(messageId, "?좏슚?섏? ?딆? 硫붿떆吏 ?앸퀎??");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException(
                "Message with id " + messageId + " not found"));

        if (req.newContent() != null) {
            message.setContent(req.newContent());
        }

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Objects.requireNonNull(messageId, "?좏슚?섏? ?딆? 硫붿떆吏 ?앸퀎??");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("?대떦 硫붿떆吏瑜?李얠쓣 ???놁뒿?덈떎!"));

        messageRepository.delete(message);
    }
}

