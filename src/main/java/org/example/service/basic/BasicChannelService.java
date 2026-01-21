package org.example.service.basic;

import org.example.entity.Channel;
import org.example.entity.ChannelType;
import org.example.entity.User;
import org.example.exception.InvalidRequestException;
import org.example.exception.NotFoundException;
import org.example.repository.ChannelRepository;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.example.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;      //Repository로 변경
    private final MessageRepository messageRepository; // Repository로 변경

    public BasicChannelService(ChannelRepository channelRepository,
                               UserRepository userRepository,
                               MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository; // 이게 맞나?
    }

    @Override
    public Channel create(String name, String description, ChannelType type, UUID ownerId) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("name", "null이 아니고 빈 값이 아님", name);
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", ownerId)); //?

        Channel channel = new Channel(name, description, type, owner);
        channelRepository.save(channel);
        owner.getChannels().add(channel);
        userRepository.save(owner);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String name, String description, ChannelType type) {
        Channel channel = findById(channelId);

        Optional.ofNullable(name).ifPresent(channel::updateName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        Optional.ofNullable(type).ifPresent(channel::updateType);

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findById(channelId);

        channel.getMembers().forEach(member -> {
            member.getChannels().remove(channel);
            userRepository.save(member);  //  추가
        });

        new ArrayList<>(channel.getMessages()).forEach(message -> {  //차이점 보기
            User sender = message.getSender();  // 삭제 전에 sender 가져오기
            message.removeFromChannelAndUser();
            messageRepository.deleteById(message.getId());
            userRepository.save(sender);  // sender도 저장
        });

        channelRepository.deleteById(channelId);
    }

    @Override
    public void addMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));

        if (channel.getMembers().contains(user)) {
            throw new InvalidRequestException("userId", "채널에 없는 유저", userId);
        }

        channel.addMember(user); //여기도 잘 동작 되는건가? 확인해보기
        channelRepository.save(channel);
        userRepository.save(user);  //  추가
    }

    @Override
    public void removeMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));

        if (channel.getOwner().getId().equals(userId)) {
            throw new InvalidRequestException("userId", "채널 오너가 아님", userId);
        }

        channel.removeMember(user);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void transferOwnership(UUID channelId, UUID newOwnerId) {
        Channel channel = findById(channelId);
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", newOwnerId));

        if (!channel.getMembers().contains(newOwner)) {
            throw new InvalidRequestException("newOwnerId", "채널 멤버여야 함", newOwnerId);
        }

        channel.updateOwner(newOwner);
        channelRepository.save(channel);
    }

    @Override
    public List<User> findMembersByChannel(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getMembers();
    }
}
