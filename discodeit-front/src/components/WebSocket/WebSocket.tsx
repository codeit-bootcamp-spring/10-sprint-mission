import {useWebSocketStore} from '@/stores/websocketStore';
import {useEffect} from "react";

interface Props {
  destination: string,
  subscribeCallback: (payload: any, type?: string) => void,
}

function WebSocket({destination, subscribeCallback}: Props) {
  const {subscribe, unsubscribe, isConnected} = useWebSocketStore();

  useEffect(() => {
    if (isConnected) {
      console.log('WEBSOCKET SUBSCRIBE', destination);
      subscribe(destination, subscribeCallback);
      return () => {
        console.log('WEBSOCKET UNSUBSCRIBE', destination);
        unsubscribe(destination);
      };
    }
  }, [destination, subscribeCallback, isConnected, subscribe, unsubscribe]);
  return <></>;
}

export default WebSocket;
