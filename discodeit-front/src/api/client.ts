import axios, {AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig} from 'axios';
import config from '@/config';
import {eventEmitter} from '../utils/eventEmitter';
import useAuthStore from "@/stores/authStore.ts";
import {getOrCreateDeviceId} from "@/utils/device.ts";
import {getCookieValue} from "@/utils/cookieUtils.ts";

const deviceId = getOrCreateDeviceId();

// 실패한 요청 캐시
let failedRequestsQueue: Array<{
  config: InternalAxiosRequestConfig;
  resolve: (value?: any) => void;
  reject: (error?: any) => void;
}> = [];
let isRefreshing = false;

// 서버 에러 응답 타입 정의
export interface ErrorResponse {
  timestamp: string;
  code: string;
  message: string;
  details: Record<string, any>;
  exceptionType: string;
  status: number;
  requestId?: string; // 요청 고유 ID
}

const client: AxiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
    'X-Device-Id': deviceId,
  },
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

export const clientWithoutAuthorization: AxiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
    'X-Device-Id': deviceId,
  },
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

// 요청 인터셉터 추가
client.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = useAuthStore.getState().accessToken;
    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`
    }
    const csrfToken = getCookieValue('XSRF-TOKEN');
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 추가
client.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    // 서버 에러 응답을 ErrorResponse 타입으로 매핑
    const errorResponse = error.response?.data as ErrorResponse | undefined;
    
    // 에러 객체에 정제된 데이터 첨부
    if (errorResponse) {
      // 응답 헤더에서 요청 ID 추출하여 에러 응답에 추가
      const requestId = error.response?.headers?.['discodeit-request-id'];
      if (requestId) {
        errorResponse.requestId = requestId;
      }
      
      error.response!.data = errorResponse;
    }
    console.log({error, errorResponse})
    
    // 에러 이벤트 발생
    eventEmitter.emit('api-error', {error, alert: error.response?.status === 403});
    
    // 401 에러 처리 (인증 실패)
    if (error.response && error.response.status === 401) {
      const originalRequest = error.config;
      
      // 이미 재시도된 요청이면 더 이상 처리하지 않음
      if (originalRequest && originalRequest.headers && originalRequest.headers['_retry']) {
        eventEmitter.emit('auth-error');
        return Promise.reject(error);
      }
      
      // 토큰 새로고침이 진행 중이면 대기열에 추가
      if (isRefreshing && originalRequest) {
        return new Promise((resolve, reject) => {
          failedRequestsQueue.push({ config: originalRequest, resolve, reject });
        });
      }
      
      // 토큰 새로고침 시작
      if (originalRequest) {
        isRefreshing = true;
        
        try {
          await useAuthStore.getState().refreshToken();
          
          // 대기 중인 모든 요청 재시도
          failedRequestsQueue.forEach(({ config, resolve, reject }) => {
            config.headers = config.headers || {};
            config.headers['_retry'] = 'true';
            client(config).then(resolve).catch(reject);
          });
          
          // 원본 요청 재시도
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers['_retry'] = 'true';
          
          // 대기열 및 플래그 초기화
          failedRequestsQueue = [];
          isRefreshing = false;
          
          return client(originalRequest);
        } catch (refreshError) {
          // 토큰 새로고침 실패
          failedRequestsQueue.forEach(({ reject }) => reject(refreshError));
          failedRequestsQueue = [];
          isRefreshing = false;
          
          eventEmitter.emit('auth-error');
          return Promise.reject(refreshError);
        }
      }
    }
    
    return Promise.reject(error);
  }
);

// baseURL을 외부에서 참조할 수 있는 함수 추가
export const getBaseUrl = (): string => {
  return client.defaults.baseURL as string;
};

export default client; 
