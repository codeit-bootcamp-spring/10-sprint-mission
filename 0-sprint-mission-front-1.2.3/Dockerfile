# ==========================================
# 1. 빌드 스테이지 (Node.js 환경)
# ==========================================
FROM node:20-alpine AS builder

# 작업 폴더 지정
WORKDIR /app

# 패키지 정보 복사 및 라이브러리 설치 (캐시 활용)
COPY package.json package-lock.json ./
RUN npm ci

# 소스 코드 전체 복사
COPY . .

# React/Vite 프로젝트 빌드 (이 과정이 끝나면 dist 폴더가 생성됩니다)
RUN npm run build 

# ==========================================
# 2. 실행 스테이지 (Nginx 환경)
# ==========================================
FROM nginx:alpine

# Nginx 기본 파일 삭제
RUN rm -rf /usr/share/nginx/html/*

# ★ 핵심: 1단계(builder)에서 완성된 'dist' 폴더의 내용물만 Nginx로 쏙 빼옵니다.
# (Vite는 빌드 결과물이 dist 폴더에 생깁니다. 만약 에러가 나면 dist를 build로 바꿔보세요)
COPY --from=builder /app/dist /usr/share/nginx/html

# 80 포트 개방 및 Nginx 실행
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
