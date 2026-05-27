package com.sprint.mission.discodeit.config.aws;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.assertj.core.api.Assertions.assertThat;

class S3ConfigTest {

    @Nested
    @DisplayName("S3Client 생성")
    class createS3Client {

        @Test
        @DisplayName("accessKey가 있으면 StaticCredentialsProvider를 사용하는 S3Client를 생성한다.")
        void create_S3Client_with_accessKey() {
            // when(준비)
            AwsProperties awsProperties = new AwsProperties();
            awsProperties.setRegion("ap-northeast-2");
            awsProperties.setAccessKey("test-access-key");
            awsProperties.setSecretKey("test-secret-key");

            S3Config s3Config = new S3Config(awsProperties);

            // when(실행)
            S3Client result = s3Config.s3Client();

            // then(검증)
            assertThat(result).isNotNull();

            result.close();
        }

        @Test
        @DisplayName("accessKey가 비어 있으면 DefaultCredentialsProvider 사용하는 S3Client를 생성한다.")
        void create_S3Client_without_accessKey() {
            // when(준비)
            AwsProperties awsProperties = new AwsProperties();
            awsProperties.setRegion("ap-northeast-2");
            awsProperties.setAccessKey("");
            awsProperties.setSecretKey("");

            S3Config s3Config = new S3Config(awsProperties);

            // when(실행)
            S3Client result = s3Config.s3Client();

            // then(검증)
            assertThat(result).isNotNull();

            result.close();
        }
    }

    @Nested
    @DisplayName("S3Presigner 생성")
    class createS3Presigner {

        @Test
        @DisplayName("accessKey가 있으면 StaticCredentialProvider를 사용하는 S3Presigner를 생성한다.")
        void create_S3Presigner_with_accessKey() {
            // when(준비)
            AwsProperties awsProperties = new AwsProperties();
            awsProperties.setRegion("ap-northeast-2");
            awsProperties.setAccessKey("");
            awsProperties.setSecretKey("");

            S3Config s3Config = new S3Config(awsProperties);

            // when(실행)
            S3Presigner result = s3Config.s3Presigner();

            // then(검증)
            assertThat(result).isNotNull();

            result.close();
        }

        @Test
        @DisplayName("accessKey가 있으면 StaticCredentialProvider를 사용하는 S3Presigner를 생성한다.")
        void create_S3Presigner_without_accessKey() {
            // when(준비)
            AwsProperties awsProperties = new AwsProperties();
            awsProperties.setRegion("ap-northeast-2");
            awsProperties.setAccessKey("");
            awsProperties.setSecretKey("");

            S3Config s3Config = new S3Config(awsProperties);

            // when(실행)
            S3Presigner result = s3Config.s3Presigner();

            // then(검증)
            assertThat(result).isNotNull();

            result.close();
        }
    }
}