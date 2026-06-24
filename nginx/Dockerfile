FROM nginx:alpine

# 프론트엔드 정적 리소스 복사
COPY static/ /usr/share/nginx/html/

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
