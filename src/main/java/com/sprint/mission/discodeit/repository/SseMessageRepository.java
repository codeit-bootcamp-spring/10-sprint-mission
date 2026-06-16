package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
@RequiredArgsConstructor
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    private static final int MAX_SIZE = 500;
    private final MessageRepository messageRepository;

    public void save(SseMessage message){
        UUID eventId = message.getEventId();

        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);
        while(messages.size() > MAX_SIZE){
            UUID oldId = eventIdQueue.pollFirst();
            if(oldId != null){
                messages.remove(oldId);
            }
        }
    }

    public List<SseMessage> findAllAfter(UUID lastEventId){
        List<SseMessage> missedMessages = new ArrayList<>();
        boolean isFound = false;
        for(UUID eventId : eventIdQueue){
            if(isFound){
                missedMessages.add(messages.get(eventId));
            } else if(eventId.equals(lastEventId)){
                isFound = true;
            }
        }

        return missedMessages;
    }
}
