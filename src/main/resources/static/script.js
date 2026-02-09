// API endpoints
const API_BASE_URL = '';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/api/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/api/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!response.ok) throw new Error('Failed to fetch profile');
        const profile = await response.json();

        const payload = profile.data ?? profile.bytes;
        if (!payload) {
            return '/default-avatar.png';
        }

        if (Array.isArray(payload)) {
            const binary = String.fromCharCode(...payload);
            return `data:${profile.contentType};base64,${btoa(binary)}`;
        }

        return `data:${profile.contentType};base64,${payload}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return '/default-avatar.png'; // Fallback to default avatar
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? await fetchUserProfile(user.profileId)
            : '/default-avatar.png';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? 'Online' : 'Offline'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}
