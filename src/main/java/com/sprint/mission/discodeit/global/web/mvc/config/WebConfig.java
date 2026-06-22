package com.sprint.mission.discodeit.global.web.mvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.global.web.mvc.constant.WebConstants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix(WebConstants.SERVLET_PREFIX,
        HandlerTypePredicate.forAnnotation(RestController.class));
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new AbstractJackson2HttpMessageConverter(objectMapper,
        MediaType.APPLICATION_OCTET_STREAM) {
      @Override
      public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return mediaType != null && mediaType.includes(MediaType.APPLICATION_OCTET_STREAM);
      }

      @Override
      public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
      }
    });
  }
}
