package com.sprint.mission.discodeit.repository.jcf;


import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
   private final Map<UUID, ReadStatus> readStatusData = new HashMap<>();

   @Override
    public void save(ReadStatus readStatus) {
       if(readStatus == null || readStatus.getId() == null) {
           throw new IllegalArgumentException("readStatus/id는 null일 수 없습니다.");
       }
       readStatusData.put(readStatus.getId(), readStatus);
   }


   @Override
    public void delete(UUID id) {
       if(id == null) throw new IllegalArgumentException("id는 null 일수 없습니다.");
       readStatusData.remove(id);
    }
    @Override
    public List<ReadStatus> findAll() {
       return new ArrayList<>(readStatusData.values());
    }

    @Override
    public Optional<ReadStatus> findById(UUID id){
       if(id == null) return Optional.empty();
       return Optional.ofNullable(readStatusData.get(id));
    }


    @Override
    public List<ReadStatus> findStatusByChannelId(UUID channelId) {
       if(channelId == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");

       List<ReadStatus> result = new ArrayList<>();
       for(ReadStatus readStatus : readStatusData.values()) {
           if(readStatus.getId().equals(channelId)) {
               result.add(readStatus);
           }
       }
       return result;
   }



}

