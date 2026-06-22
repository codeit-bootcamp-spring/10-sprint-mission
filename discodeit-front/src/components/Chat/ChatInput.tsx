import { WS_DESTINATIONS } from '@/constants/websocket';
import {useWebSocketStore} from '@/stores/websocketStore';
import {useState} from 'react';

interface ChatInputProps {
  channelId?: string;
}

function ChatInput({channelId}: ChatInputProps) {
  const [text, setText] = useState('');
  const {send, isConnected} = useWebSocketStore();

  const handleSend = () => {
    if (!text.trim() || !isConnected) return;

    // 1. 보낼 데이터 포맷 맞추기 (백엔드의 MessageCreateRequest DTO와 동일하게)
    const requestPayload = {
      content: text
    };

    // 2. 백엔드의 @MessageMapping 주소로 전송
    if (channelId) {
      send(WS_DESTINATIONS.PUB_MESSAGE(channelId), requestPayload);
    }

    // 3. 전송 후 입력창 비우기
    setText('');
  };

  return (
      <div>
        <input
            value={text}
            onChange={(e) => setText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
        />
        <button onClick={handleSend}>전송</button>
      </div>
  );
}

export default ChatInput;
