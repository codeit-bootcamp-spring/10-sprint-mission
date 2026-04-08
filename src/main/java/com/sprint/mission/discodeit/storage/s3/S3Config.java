package com.sprint.mission.discodeit.storage.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
    private final AwsProperties props;

    public S3Config(AwsProperties props) {
        this.props = props;
    }

    //S3Client를 스프링 컨테이너에 등록한다.
    //@Autowired
    //S3Client s3Client; 이런식으로 어디서든 사용가능하다.
    @Bean
    public S3Client s3Client() {//S3Client: S3 API 호출용 클라이언트(S3에 요청 보내는 객체)

        // 키가 있는경우
        if (props.getAccessKey() != null && !props.getAccessKey().isBlank()) {
            return S3Client.builder()
                    .region(Region.of(props.getRegion()))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            props.getAccessKey(),
                                            props.getSecretKey()
                                    )
                            )
                    )
                    .build();
        }
        // 그렇지 않으면: 기본 체인(환경변수, 프로파일, IAM Role)을 자동 탐색
        // 키가 없는경우
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // 아래는 삼항연산자를 사용하지 않은 케이스
    // 위와 아래 메서드중 하나를 선택해서 작성하고 사용하세요.
    @Bean
    public S3Presigner s3Presigner() {
        if(props.getAccessKey() != null && !props.getSecretKey().isBlank()) {
            return S3Presigner.builder()
                    .region(Region.of(props.getRegion()))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            props.getAccessKey(),
                                            props.getSecretKey()
                                    )
                            )
                    ).build();
        }
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
