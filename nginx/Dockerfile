# Nginx 공식 이미지를 기반으로 컨테이너를 만듭니다
FROM nginx:1.27-alpine

# 프로젝트 루트의 nginx/default.conf를 Nginx 컨테이너 내부 설정 경로로 복사합니다.
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Spring boot가 기존에 서빙하던 정적 프론트 빌드 결과물을 nginx 기본 정적 파일 경로로 복사합니다.
# index.html, /assets/...의 정적파일을 Spring Boot까지 오지않고 직접 내려줄 수 있다.
COPY src/main/resources/static /usr/share/nginx/html