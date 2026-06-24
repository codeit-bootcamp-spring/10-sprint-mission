FROM nginx:alpine

# 커스텀 Nginx 설정 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 프론트엔드 정적 빌드 파일 복사
COPY src/main/resources/static /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
