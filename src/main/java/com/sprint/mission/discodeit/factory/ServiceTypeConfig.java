package com.sprint.mission.discodeit.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceTypeConfig {
    @Bean
    public ServiceType serviceTypeJCF() {
        return ServiceType.JCF;
    }

    @Bean
    public ServiceType serviceTypeFile() {
        return ServiceType.FILE;
    }
}
