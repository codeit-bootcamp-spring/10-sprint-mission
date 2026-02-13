const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

const DEFAULT_AVATAR = '/default-avatar.png';

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    const res = await fetch(ENDPOINTS.USERS);
    if (!res.ok) {
        console.error('Failed to fetch users');
        return;
    }
    const users = await res.json();
    renderUserList(users);
}

async function fetchUserProfileDataUrl(profileId) {
    const res = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
    if (!res.ok) {
        return DEFAULT_AVATAR;
    }

    const profile = await res.json();
    // profile: { id, fileName, contentType, bytes }
    // bytes can be base64 string (common) OR number array (if customized)

    let base64;
    if (typeof profile.bytes === 'string') {
        base64 = profile.bytes;
    } else if (Array.isArray(profile.bytes)) {
        base64 = bytesArrayToBase64(profile.bytes);
    } else {
        return DEFAULT_AVATAR;
    }

    const contentType = profile.contentType || 'image/png';
    return `data:${contentType};base64,${base64}`;
}

// number[] -> base64
function bytesArrayToBase64(arr) {
    const u8 = new Uint8Array(arr);
    let binary = '';
    for (let i = 0; i < u8.length; i++) {
        binary += String.fromCharCode(u8[i]);
    }
    return btoa(binary);
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? await fetchUserProfileDataUrl(user.profileId)
            : DEFAULT_AVATAR;

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        // 이미지가 깨지면 기본 이미지로 대체 (최종 안전장치)
        const img = userElement.querySelector('img');
        img.onerror = () => {
            img.onerror = null;
            img.src = DEFAULT_AVATAR;
        };

        userListElement.appendChild(userElement);
    }
}
