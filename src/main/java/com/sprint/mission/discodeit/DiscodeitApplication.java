package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(DiscodeitApplication.class, args);

        Arrays.stream(ac.getBeanNamesForAnnotation(Service.class))
                .forEach(System.out::println);
        Arrays.stream(ac.getBeanNamesForAnnotation(Repository.class))
                .forEach(System.out::println);
    }
}
