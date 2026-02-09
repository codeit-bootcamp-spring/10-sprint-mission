const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`,
    LOGIN: `${API_BASE_URL}/auth/login`,
    CHANNELS: `${API_BASE_URL}/channels`,
    MESSAGES: `${API_BASE_URL}/channels` // /{channelId}/messages
};

const loginView = document.getElementById('login-view');
const channelView = document.getElementById('channel-view');
const messageView = document.getElementById('message-view');
const userView = document.getElementById('user-view');

const loginBtn = document.getElementById('loginBtn');
const showUsersBtn = document.getElementById('showUsersBtn');
const closeUserListBtn = document.getElementById('closeUserListBtn');
const backToChannelsBtn = document.getElementById('backToChannelsBtn');
const sendMessageBtn = document.getElementById('sendMessageBtn');

let currentUser = null;
let currentChannelId = null;

// 로그인
loginBtn.addEventListener('click', async () => {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    try {
        const res = await fetch(ENDPOINTS.LOGIN, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });
        if (!res.ok) throw new Error('로그인 실패');
        currentUser = await res.json();
        loginView.style.display = 'none';
        await fetchAndRenderChannels();
        channelView.style.display = 'block';
    } catch (err) {
        document.getElementById('loginError').innerText = err.message;
    }
});

// 채널 목록
async function fetchAndRenderChannels() {
    const res = await fetch(ENDPOINTS.CHANNELS, {headers: {'userId': currentUser.id}});
    const channels = await res.json();
    const ul = document.getElementById('channelList');
    ul.innerHTML = '';

    const publicChannels = channels.filter(c => c.channelType === 'PUBLIC');
    const privateChannels = channels.filter(c => c.channelType === 'PRIVATE');

    // 공개 채널 영역
    if (publicChannels.length) {
        const publicWrapper = document.createElement('div');
        publicWrapper.className = 'channel-section public-section';
        publicWrapper.innerHTML = `<div class="section-header">공개 채널</div>`;

        publicChannels.forEach(c => {
            const li = document.createElement('div');
            li.className = 'channel-item';
            li.style.cursor = 'pointer';
            const description = c.description?.length > 50 ? c.description.slice(0,50)+'...' : c.description || '';
            li.innerHTML = `
                <div class="channel-name">${c.name}</div>
                <div class="channel-desc">${description}</div>
            `;
            li.addEventListener('click', () => openChannel(c.id));
            publicWrapper.appendChild(li);
        });

        ul.appendChild(publicWrapper);
    }

    // 비공개 채널 영역
    if (privateChannels.length) {
        const privateWrapper = document.createElement('div');
        privateWrapper.className = 'channel-section private-section';
        privateWrapper.innerHTML = `<div class="section-header">비공개 채널</div>`;

        for (const c of privateChannels) {
            const li = document.createElement('div');
            li.className = 'channel-item';
            li.style.cursor = 'pointer';

            const membersExSelf = c.memberIds.filter(id => id !== currentUser.id);
            let memberNames = await Promise.all(membersExSelf.map(id => fetchUserById(id).then(u => u.username)));

            let displayText = '';
            if (memberNames.length === 0) displayText = '(참여자 없음)';
            else if (memberNames.length <= 3) displayText = memberNames.join(', ');
            else displayText = `${memberNames[0]} 외 ${memberNames.length - 1}명`;

            li.innerHTML = `<div class="channel-name">${displayText}</div>`;
            li.addEventListener('click', () => openChannel(c.id));
            privateWrapper.appendChild(li);
        }

        ul.appendChild(privateWrapper);
    }
}


// 채널 클릭 → 메시지 화면
async function openChannel(channelId) {
    currentChannelId = channelId;
    channelView.style.display = 'none';
    messageView.style.display = 'block';
    await fetchAndRenderMessages(channelId);
}

// 메시지 목록
async function fetchAndRenderMessages(channelId) {
    const res = await fetch(
        `${ENDPOINTS.MESSAGES}/${channelId}/messages`,
        { headers: { 'userId': currentUser.id } }
    );

    const messages = await res.json();
    const ul = document.getElementById('messageList');
    ul.innerHTML = '';

    for (const m of messages) {
        const el = await createMessageElement(m);
        ul.appendChild(el);
    }

    // 맨 아래로
    ul.scrollTop = ul.scrollHeight;
}


// 작성자 정보 가져오기
async function fetchUserById(userId) {
    const users = await fetchUsersApi(); // 전체 사용자 불러오기
    return users.find(u => u.id === userId) || {username: 'Unknown', profileId: null};
}


// 메시지 전송
sendMessageBtn.addEventListener('click', async () => {
    const input = document.getElementById('messageInput');
    if (!input.value.trim()) return;

    const content = input.value;
    input.value = '';

    const res = await fetch(
        `${ENDPOINTS.MESSAGES}/${currentChannelId}/messages`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'userId': currentUser.id
            },
            body: JSON.stringify({
                content,
                attachmentsIds: []
            })
        }
    );

    // 서버가 생성한 메시지 반환한다고 가정
    const savedMessage = await res.json();

    const ul = document.getElementById('messageList');
    const el = await createMessageElement(savedMessage);
    ul.appendChild(el);

    // 자연스럽게 아래로
    ul.scrollTop = ul.scrollHeight;
});

//send message with enter
const messageInput = document.getElementById('messageInput');

messageInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault(); // 줄바꿈 방지
        sendMessageBtn.click();
    }
});


// 메시지 화면 → 채널 목록
backToChannelsBtn.addEventListener('click', () => {
    messageView.style.display = 'none';
    channelView.style.display = 'block';
});

// 참여자 목록
showUsersBtn.addEventListener('click', async () => {
    channelView.style.display = 'none';
    userView.style.display = 'block';
    const users = await fetchUsersApi();
    renderUserList(users);
});

closeUserListBtn.addEventListener('click', () => {
    userView.style.display = 'none';
    channelView.style.display = 'block';
});

// 사용자 목록 API 호출
async function fetchUsersApi() {
    const res = await fetch(ENDPOINTS.USERS);
    return res.json();
}

// 프로필 이미지
async function fetchUserProfile(profileId) {
    try {
        const res = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!res.ok) throw new Error('Failed to fetch profile');
        const profile = await res.json();
        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch {
        return 'default-avatar.png';
    }
}

// 사용자 목록 렌더링 (미려 스타일)
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';
    for (const user of users) {
        const div = document.createElement('div');
        div.className = 'user-item';
        const profileUrl = user.profileId ? await fetchUserProfile(user.profileId) : 'default-avatar.png';
        div.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;
        userListElement.appendChild(div);
    }
}
async function createMessageElement(m) {
    let author = await fetchUserById(m.authorId);
    const profileUrl = author.profileId
        ? await fetchUserProfile(author.profileId)
        : 'default-avatar.png';

    const li = document.createElement('div');
    li.className = 'message-item';

    if (m.authorId === currentUser.id) {
        li.classList.add('mine');
    }

    li.innerHTML = `
        <img src="${profileUrl}" class="message-avatar">
        <div class="message-content">
            <div class="message-author">${author.username}</div>
            <div class="message-text">${m.content}</div>
            <div class="message-time">
                ${new Date(m.createdAt).toLocaleTimeString()}
            </div>
        </div>
    `;

    return li;
}
const signupView = document.getElementById('signup-view');
const goSignupBtn = document.getElementById('goSignupBtn');
const backToLoginBtn = document.getElementById('backToLoginBtn');
const signupBtn = document.getElementById('signupBtn');
goSignupBtn.addEventListener('click', () => {
    loginView.style.display = 'none';
    signupView.style.display = 'flex';
});
backToLoginBtn.addEventListener('click', () => {
    signupView.style.display = 'none';
    loginView.style.display = 'flex';
});
function fileToBytes(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => {
            const bytes = Array.from(new Uint8Array(reader.result));
            resolve(bytes);
        };
        reader.onerror = reject;
        reader.readAsArrayBuffer(file);
    });
}
signupBtn.addEventListener('click', async () => {
    const username = document.getElementById('signupUsername').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const profileFile = document.getElementById('signupProfile').files[0];

    try {
        let profileDto = null;

        if (profileFile) {
            const bytes = await fileToBytes(profileFile);
            profileDto = {
                fileName: profileFile.name,
                bytes
            };
        }

        const res = await fetch('/api/user', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username,
                email,
                password,
                profileDto
            })
        });

        if (!res.ok) {
            throw new Error('회원가입 실패');
        }

        alert('회원가입이 완료되었습니다! 로그인 해주세요.');

        // 로그인 화면으로 이동
        signupView.style.display = 'none';
        loginView.style.display = 'flex';

    } catch (e) {
        document.getElementById('signupError').innerText = e.message;
    }
});
