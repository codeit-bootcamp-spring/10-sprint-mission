import { create } from 'zustand';
import {Client, StompSubscription} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import config from '@/config';
import useAuthStore from './authStore';
import {eventEmitter} from "@/utils/eventEmitter.ts";
import {getOrCreateDeviceId} from "@/utils/device.ts";

const deviceId = getOrCreateDeviceId();

interface WebSocketState {
  stompClient: Client | null;
  isConnected: boolean;
  isConnecting: boolean;
  subscriptions: Map<string, StompSubscription>;
  connect: () => Promise<void>;
  disconnect: () => void;
  subscribe: (destination: string, callback: (payload: any, type?: string) => void) => void;
  unsubscribe: (destination: string) => void;
  send: (destination: string, body: any) => void;
}

export const useWebSocketStore = create<WebSocketState>((set, get) => ({
  stompClient: null,
  isConnected: false,
  isConnecting: false,
  subscriptions: new Map(),

  connect: async () => {
    const { isConnected, isConnecting } = get();
    if (isConnected || isConnecting) return;

    set({ isConnecting: true });

    const accessToken = useAuthStore.getState().accessToken;
    if (!accessToken) {
      set({ isConnected: false });
      return;
    }

    const socket = new SockJS(config.wsBaseUrl);
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
        'X-Device-Id': deviceId,
      },
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      set({ stompClient: client, isConnected: true, isConnecting: false });
      console.log('WebSocket 연결 성공');
    };

    client.onStompError = (frame) => {
      console.error('STOMP 에러:', frame);
      set({ isConnected: false, stompClient: null, isConnecting: false });
      const errorResponse = {
        message: frame.headers.message
      };
      const error = {
        response: {
          status: 'WebSocket',
          data: errorResponse
        }
      }
      eventEmitter.emit("api-error", {error, alert: true})
    };

    client.onWebSocketClose = () => {
      set({ isConnected: false, stompClient: null, isConnecting: false });
    };

    client.activate();
  },

  disconnect: () => {
    const { stompClient, isConnected } = get();
    if (stompClient && isConnected) {
      stompClient.deactivate();
      set({ stompClient: null, isConnected: false, subscriptions: new Map() });
    }
  },

  subscribe: (destination: string, callback: (payload: any, type?: string) => void) => {
    const { stompClient, isConnected, subscriptions } = get();

    if (subscriptions.has(destination) || !isConnected || stompClient == null) {
      return;
    }

    const subscription: StompSubscription = stompClient.subscribe(destination, (message) => {
      const payload = JSON.parse(message.body);
      const type = message.headers ? message.headers['type'] : undefined;
      callback(payload, type);
    });

    subscriptions.set(destination, subscription)
  },

  unsubscribe: (destination: string) => {
    const { stompClient, isConnected, subscriptions } = get();

    if (!subscriptions.has(destination) || !isConnected || stompClient == null) {
      return;
    }

    const subscription = subscriptions.get(destination)
    if (subscription) {
      subscription.unsubscribe();
      subscriptions.delete(destination);
      set({ subscriptions });
    }
  },

  send: (destination: string, body: any) => {
    const { stompClient, isConnected } = get();

    if (stompClient && isConnected) {
      stompClient.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.error('WebSocket이 연결되어 있지 않습니다.');
      const errorResponse = {
        message: 'WebSocket이 연결되어 있지 않습니다.'
      };
      const error = {
        response: {
          status: 'WebSocket',
          data: errorResponse
        }
      }
      eventEmitter.emit("api-error", {error, alert: true})
    }
  },
}));
