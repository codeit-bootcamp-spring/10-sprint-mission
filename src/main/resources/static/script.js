document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const loginContainer = document.getElementById('login-container');
    const appContainer = document.getElementById('app-container');
    const loginForm = document.getElementById('login-form');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    const channelList = document.getElementById('channel-list');
    const userList = document.getElementById('user-list');
    const chatHeader = document.getElementById('chat-header');
    const messageList = document.getElementById('message-list');
    const messageForm = document.getElementById('message-form');
    const messageInput = document.getElementById('message-input');

    // App State
    let currentUser = null;
    let currentChannelId = null;
    let pollingInterval;

    const API_BASE_URL = '/api';

    // --- API Functions ---
    const api = {
        login: async (username, password) => {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Login failed' }));
                throw new Error(errorData.message);
            }
            return response.json();
        },
        getChannels: async () => {
            const response = await fetch(`${API_BASE_URL}/channels`);
            if (!response.ok) throw new Error('Failed to fetch channels');
            return response.json();
        },
        getUsers: async () => {
            const response = await fetch(`${API_BASE_URL}/users`);
            if (!response.ok) throw new Error('Failed to fetch users');
            return response.json();
        },
        getMessages: async (channelId) => {
            const response = await fetch(`${API_BASE_URL}/messages?channelId=${channelId}`);
            if (!response.ok) throw new Error('Failed to fetch messages');
            return response.json();
        },
        sendMessage: async (content) => {
            const response = await fetch(`${API_BASE_URL}/messages`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    channelId: currentChannelId,
                    senderId: currentUser.userId,
                    content: content,
                }),
            });
            if (!response.ok) {
                throw new Error('Failed to send message');
            }
            return response.json();
        }
    };

    // --- Render Functions ---
    const renderChannels = (channels) => {
        channelList.innerHTML = '';
        channels.forEach(channel => {
            const li = document.createElement('li');
            li.className = 'channel-item';
            li.textContent = channel.channelName;
            li.dataset.id = channel.channelId;
            li.dataset.name = channel.channelName;
            if (channel.channelId === currentChannelId) {
                li.classList.add('active');
            }
            channelList.appendChild(li);
        });
    };

    const renderUsers = (users) => {
        userList.innerHTML = '';
        users.sort((a, b) => a.username.localeCompare(b.username));
        users.forEach(user => {
            const li = document.createElement('li');
            li.className = 'user-item';
            // Using a simple placeholder avatar. The API for binary content could be integrated here.
            li.innerHTML = `
                <img src="https://i.pravatar.cc/40?u=${user.userId}" alt="${user.username}" class="user-avatar">
                <span class="user-name">${user.username}</span>
                <div class="status-indicator ${user.online ? 'status-online' : 'status-offline'}"></div>
            `;
            userList.appendChild(li);
        });
    };

    const renderMessages = (messages) => {
        messageList.innerHTML = '';
        messages.sort((a,b) => new Date(a.createdAt) - new Date(b.createdAt));
        messages.forEach(msg => {
            const div = document.createElement('div');
            div.className = 'message-item';
            const timestamp = new Date(msg.createdAt).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
            div.innerHTML = `
                <img src="https://i.pravatar.cc/40?u=${msg.senderId}" alt="User" class="message-avatar">
                <div class="message-content">
                    <div class="message-header">
                        <span class="message-username">${msg.senderUsername || 'Unknown User'}</span>
                        <span class="message-timestamp">${timestamp}</span>
                    </div>
                    <p class="message-text">${msg.content}</p>
                </div>
            `;
            messageList.appendChild(div);
        });
        messageList.scrollTop = messageList.scrollHeight;
    };


    // --- App Logic ---
    const showLogin = () => {
        loginContainer.classList.remove('hidden');
        appContainer.classList.add('hidden');
        localStorage.removeItem('discodeitUser');
        currentUser = null;
        currentChannelId = null;
        if (pollingInterval) clearInterval(pollingInterval);
    };

    const showApp = () => {
        loginContainer.classList.add('hidden');
        appContainer.classList.remove('hidden');
        initializeAppData();
    };

    const initializeAppData = async () => {
        try {
            const [channels, users] = await Promise.all([api.getChannels(), api.getUsers()]);
            renderChannels(channels);
            renderUsers(users);

            if (!currentChannelId && channels.length > 0) {
                const firstChannel = channels[0];
                await selectChannel(firstChannel.channelId, firstChannel.channelName);
            }
            
            pollingInterval = setInterval(pollForUpdates, 5000); // Poll every 5 seconds
        } catch (error) {
            console.error('Error initializing app data:', error);
            alert('앱 데이터 로딩에 실패했습니다.');
        }
    };
    
    const pollForUpdates = async () => {
        if (!currentUser) return;
        
        try {
            const fetchPromises = [api.getUsers()];
            if (currentChannelId) {
                fetchPromises.push(api.getMessages(currentChannelId));
            }

            const [users, messages] = await Promise.all(fetchPromises);
            
            renderUsers(users);
            if (messages) {
                renderMessages(messages);
            }
        } catch (error) {
            console.error('Polling error:', error);
        }
    };

    const selectChannel = async (channelId, channelName) => {
        currentChannelId = channelId;
        chatHeader.textContent = channelName;
        document.querySelectorAll('.channel-item').forEach(item => {
            item.classList.toggle('active', item.dataset.id === channelId);
        });
        
        messageList.innerHTML = '<p>메시지를 불러오는 중...</p>';
        try {
            const messages = await api.getMessages(channelId);
            renderMessages(messages);
        } catch (error) {
            console.error(`Error fetching messages for channel ${channelId}:`, error);
            messageList.innerHTML = '<p>메시지를 불러오는데 실패했습니다.</p>';
        }
    };


    // --- Event Listeners ---
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = usernameInput.value;
        const password = passwordInput.value;
        try {
            const userData = await api.login(username, password);
            currentUser = userData;
            localStorage.setItem('discodeitUser', JSON.stringify(userData));
            showApp();
        } catch (error) {
            console.error(error);
            alert(`로그인 실패: ${error.message}`);
        }
    });

    channelList.addEventListener('click', (e) => {
        const channelItem = e.target.closest('.channel-item');
        if (channelItem && channelItem.dataset.id !== currentChannelId) {
            const { id, name } = channelItem.dataset;
            selectChannel(id, name);
        }
    });

    messageForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const content = messageInput.value.trim();
        if (content && currentChannelId) {
            try {
                messageInput.value = ''; // Clear input immediately for better UX
                await api.sendMessage(content);
                // The new message will be picked up by the next poll,
                // but we can also fetch immediately for a snappier feel.
                const messages = await api.getMessages(currentChannelId);
                renderMessages(messages);
            } catch (error) {
                console.error('Error sending message:', error);
                alert('메시지 전송에 실패했습니다.');
                messageInput.value = content; // Restore input if sending failed
            }
        }
    });

    // --- Initial Check ---
    const storedUser = localStorage.getItem('discodeitUser');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
        showApp();
    } else {
        showLogin();
    }
});
