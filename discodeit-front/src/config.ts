interface Config {
  apiBaseUrl: string;
  wsBaseUrl: string;
  sseBaseUrl: string;
  // 다른 설정 값들 추가
}

const config: Config = {
  apiBaseUrl: '/api', // 또는 실제 API 기본 URL
  wsBaseUrl: '/ws',
  sseBaseUrl: '/api/sse'
  // 다른 설정 값들 추가
};

export default config; 