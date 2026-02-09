// API 기본 경로
const API_BASE_URL = '/api';

// [중요] 현재 로그인한 사용자 정보 (임시 하드코딩)
// 실제로는 로그인 후 세션/토큰에서 가져오거나, DB에 저장된 본인의 UUID를 넣어야 합니다.
// 서버 로그 등을 확인하여 유효한 User UUID를 이곳에 입력해주세요.
const currentUser = {
    id: "bac055ad-4c27-451e-9a68-2f29508da8f5", // <--- 여기에 실제 DB의 User ID(UUID)를 붙여넣으세요!
    username: "Woody" // 화면에 표시될 본인 이름
};

// DOM 요소 선택
const channelListEl = document.getElementById('channelList');
const messageListEl = document.getElementById('messageList');
const messageInput = document.getElementById('messageInput');
const currentChannelNameEl = document.getElementById('currentChannelName');

// 상태 변수
let currentChannelId = null;

// 초기화
document.addEventListener('DOMContentLoaded', () => {
    // 1. 채널 목록 불러오기
    fetchChannels();

    // 2. 메시지 입력창 엔터키 이벤트 리스너
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault(); // 줄바꿈 방지
            sendMessage();
        }
    });
});

/**
 * 1. 채널 목록 불러오기 (GET /api/channels?userId=...)
 */
async function fetchChannels() {
    try {
        if (!currentUser.id || currentUser.id === "YOUR_USER_UUID_HERE") {
            console.warn("currentUser.id가 설정되지 않았습니다. script.js를 수정해주세요.");
            // 테스트를 위해 더미 UI만 렌더링하거나 중단
            alert("script.js 파일에서 currentUser.id에 실제 UUID를 입력해야 동작합니다.");
            return;
        }

        const response = await fetch(`${API_BASE_URL}/channels?userId=${currentUser.id}`);
        if (!response.ok) throw new Error('채널 목록을 불러오는데 실패했습니다.');

        const channels = await response.json();
        renderChannels(channels);

        // 채널이 하나라도 있으면 첫 번째 채널 자동 선택
        if (channels.length > 0) {
            selectChannel(channels[0]);
        }
    } catch (error) {
        console.error('채널 로딩 에러:', error);
        channelListEl.innerHTML = '<div class="channel-item">채널 로딩 실패</div>';
    }
}

/**
 * 2. 채널 목록 렌더링
 */
function renderChannels(channels) {
    channelListEl.innerHTML = ''; // 초기화

    channels.forEach(channel => {
        const div = document.createElement('div');
        div.className = 'channel-item';
        div.textContent = `# ${channel.name}`; // PUBLIC 채널은 이름이 있음

        // PRIVATE 채널일 경우 이름 처리 로직 (선택 사항)
        if(channel.type === 'PRIVATE') {
            div.textContent = '🔒 비공개 채팅';
        }

        // 클릭 이벤트 연결
        div.onclick = () => selectChannel(channel);
        div.dataset.id = channel.id; // active 스타일 처리를 위한 식별자

        channelListEl.appendChild(div);
    });
}

/**
 * 3. 채널 선택 처리
 */
function selectChannel(channel) {
    // 상태 업데이트
    currentChannelId = channel.id;
    currentChannelNameEl.textContent = channel.name || "비공개 채팅";

    // UI 활성화 표시 (active 클래스 토글)
    document.querySelectorAll('.channel-item').forEach(el => {
        el.classList.remove('active');
        if (el.dataset.id === channel.id.toString()) {
            el.classList.add('active');
        }
    });

    // 해당 채널의 메시지 불러오기
    fetchMessages(channel.id);
}

/**
 * 4. 메시지 목록 불러오기 (GET /api/messages?channelId=...)
 */
async function fetchMessages(channelId) {
    messageListEl.innerHTML = ''; // 기존 메시지 비우기

    try {
        const response = await fetch(`${API_BASE_URL}/messages?channelId=${channelId}`);
        if (!response.ok) throw new Error('메시지 목록을 불러오는데 실패했습니다.');

        const messages = await response.json();

        // 날짜순 정렬 (혹시 백엔드에서 정렬 안 되었을 경우)
        // messages.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));

        messages.forEach(msg => {
            // [중요] 현재 API 응답에는 authorName이 없으므로 ID로 구분하거나 임시 처리
            // 내 메시지면 내 이름, 아니면 'User' + ID 일부
            let displayAuthorName = 'Unknown';
            if (msg.authorId === currentUser.id) {
                displayAuthorName = currentUser.username;
            } else {
                displayAuthorName = `User ${msg.authorId.substring(0, 4)}`;
            }

            // 화면용 객체로 변환하여 렌더링
            appendMessage({
                id: msg.id,
                content: msg.content,
                author: displayAuthorName,
                createdAt: formatTime(msg.createdAt)
            });
        });

    } catch (error) {
        console.error('메시지 로딩 에러:', error);
    }
}

/**
 * 5. 메시지 화면 추가 (UI 렌더링)
 */
function appendMessage(msg) {
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message';

    // HTML 구조 생성
    msgDiv.innerHTML = `
        <div class="message-content">
            <h4>
                ${msg.author} 
                <span class="message-time">${msg.createdAt}</span>
            </h4>
            <div class="message-text">${msg.content}</div>
        </div>
    `;

    messageListEl.appendChild(msgDiv);

    // 스크롤을 항상 최신 메시지(바닥)로 이동
    messageListEl.scrollTop = messageListEl.scrollHeight;
}

/**
 * 6. 메시지 전송 (POST /api/messages)
 */
async function sendMessage() {
    const content = messageInput.value.trim();

    // 유효성 검사: 내용이 없거나 채널이 선택되지 않았으면 중단
    if (!content || !currentChannelId) return;

    // 요청 데이터 생성 (DTO 구조에 맞춤)
    const messageCreateRequest = {
        content: content,
        channelId: currentChannelId,
        authorId: currentUser.id,
        attachments: null // 첨부파일 없음 (추후 구현 가능)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/messages`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(messageCreateRequest)
        });

        if (!response.ok) throw new Error('메시지 전송 실패');

        const savedMessage = await response.json();

        // 전송 성공 시 화면에 내 메시지 즉시 추가
        appendMessage({
            id: savedMessage.id,
            content: savedMessage.content,
            author: currentUser.username, // 내 이름
            createdAt: formatTime(new Date().toISOString()) // 현재 시간
        });

        // 입력창 초기화
        messageInput.value = '';

    } catch (error) {
        console.error('전송 에러:', error);
        alert('메시지 전송 중 오류가 발생했습니다.');
    }
}

/**
 * 유틸리티: 시간 포맷팅 (오전/오후 hh:mm)
 */
function formatTime(isoString) {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}