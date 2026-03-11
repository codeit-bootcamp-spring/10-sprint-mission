package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public PageResponse fromSlice(Slice<MessageDto> slice) {
    return new PageResponse(
        new ArrayList<Object>(slice.getContent()),
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }

  public PageResponse fromPage(Page<MessageDto> page) {
    return new PageResponse(
        new ArrayList<Object>(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }
}