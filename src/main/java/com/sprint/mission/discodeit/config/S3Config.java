package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.s3.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

}
