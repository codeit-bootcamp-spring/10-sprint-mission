package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusCreateInput;
import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusUpdateInput;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    // creat
    ReadStatus createReadStatus(ReadStatusCreateInput input);

    // read
    ReadStatus findReadStatusById(UUID readStatusId);

    // all read
    List<ReadStatus> findAllByUserId(UUID userId);

    // update
    ReadStatus updateReadStatus(ReadStatusUpdateInput input);

    // delete
    void deleteReadStatus(UUID readStatusId);
}
