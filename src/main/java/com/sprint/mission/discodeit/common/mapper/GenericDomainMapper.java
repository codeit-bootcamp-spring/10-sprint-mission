package com.sprint.mission.discodeit.common.mapper;

import org.mapstruct.MappingTarget;

public interface GenericDomainMapper<D, E> {

  void partialUpdate(D dto, @MappingTarget E entity);
}
