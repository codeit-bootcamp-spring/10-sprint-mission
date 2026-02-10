const API = {
    USERS: '/api/user/findAll',
    PROFILE: '/api/binaryContent/find'
};

function formatDate(isoString) {
    if (!isoString) return "기록 없음";
    const date = new Date(isoString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

async function init() {
    try {
        const res = await fetch(API.USERS);
        if (!res.ok) throw new Error('사용자 목록 로드 실패');

        const users = await res.json();
        const listContainer = document.getElementById('userList');
        listContainer.innerHTML = '';

        for (const user of users) {
            const userCard = document.createElement('div');
            userCard.className = 'user-item';

            // [핵심 수정] 프로필 이미지 비동기 처리 로직 보강
            let profileImg = 'https://via.placeholder.com/80?text=User'; // 기본 이미지

            if (user.profileId) {
                try {
                    const imgRes = await fetch(`${API.PROFILE}?binaryContentId=${user.profileId}`);
                    if (imgRes.ok) {
                        const content = await imgRes.json();
                        /**
                         * 진우야, 여기서 content.data 인지 content.bytes 인지 확인해줘!
                         * 아까 네가 준 JSON 예시에서는 "data" 였어.
                         */
                        const base64Data = content.data || content.bytes;
                        if (base64Data) {
                            profileImg = `data:${content.contentType || 'image/png'};base64,${base64Data}`;
                        }
                    }
                } catch (e) {
                    console.error("이미지 로딩 실패:", e);
                }
            }

            userCard.innerHTML = `
                <div class="avatar-container">
                    <img src="${profileImg}" class="user-avatar" alt="profile" onerror="this.src='https://via.placeholder.com/80?text=Error'">
                    <div class="status-dot ${user.online ? 'online' : 'offline'}"></div>
                </div>
                <div class="user-info">
                    <div class="user-header">
                        <span class="user-name">${user.username}</span>
                        <span class="user-id-tag">#${user.id.slice(0, 8)}</span>
                    </div>
                    <div class="user-details">
                        <div class="user-email">✉️ ${user.email}</div>
                        <div class="user-stats">
                            <span class="user-meta">📅 가입: ${formatDate(user.createdAt)}</span>
                        </div>
                    </div>
                </div>
                <div class="action-section">
                    <div class="status-text ${user.online ? 'text-online' : 'text-offline'}">
                        ${user.online ? '온라인' : '오프라인'}
                    </div>
                    <button class="detail-btn">상세보기</button>
                </div>
            `;
            listContainer.appendChild(userCard);
        }
    } catch (e) {
        console.error("데이터 로드 실패:", e);
    }
}

init();