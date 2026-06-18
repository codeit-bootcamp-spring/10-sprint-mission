FROM nginx:alpine

COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

COPY index.html /usr/share/nginx/html/index.html
COPY src/main/resources/static/assets /usr/share/nginx/html/assets

EXPOSE 80