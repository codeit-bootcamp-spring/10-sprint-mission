package com.sprint.mission.service.validation;

public interface ServiceValidator<T> {
    void validateIdExist(T request);
}
