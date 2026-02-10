// API 엔드포인트 설정
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// 기본 프로필 이미지 (로컬 파일이 없을 경우를 대비해 온라인 플레이스홀더 사용)
// 실제 운영 시에는 로컬 경로('/images/default-avatar.png')로 변경하세요.
const DEFAULT_AVATAR = 'https://ui-avatars.com/api/?background=random&color=fff&name=User';

// DOM 요소 참조
const userListElement = document.getElementById('userList');
const statusMessageElement = document.getElementById('statusMessage');

// 애플리케이션 초기화
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

/**
 * 사용자 목록을 가져와서 렌더링합니다.
 */
async function fetchAndRenderUsers() {
    try {
        // 1. 로딩 상태 표시
        statusMessageElement.style.display = 'block';
        statusMessageElement.textContent = '데이터를 불러오는 중입니다...';
        userListElement.innerHTML = '';

        // 2. 사용자 목록 API 호출
        const response = await fetch(ENDPOINTS.USERS);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const users = await response.json();

        // 3. 데이터가 비어있는지 확인
        if (!users || users.length === 0) {
            statusMessageElement.textContent = '등록된 사용자가 없습니다.';
            return;
        }

        // 4. 데이터가 있으면 로딩 메시지 숨기고 렌더링 시작
        statusMessageElement.style.display = 'none';
        renderUserList(users);

    } catch (error) {
        console.error('사용자 목록 조회 실패:', error);
        statusMessageElement.textContent = '데이터를 불러오는 데 실패했습니다.';
        statusMessageElement.style.color = 'red';
    }
}

/**
 * 사용자 리스트를 화면에 그립니다.
 * 이미지는 비동기로 별도로 로드하여 체감 속도를 높입니다.
 */
function renderUserList(users) {
    // DocumentFragment를 사용하여 리플로우(Reflow) 최소화 (성능 최적화)
    const fragment = document.createDocumentFragment();

    users.forEach(user => {
        const userItem = document.createElement('li'); // div -> li로 변경 (HTML 구조 준수)
        userItem.className = 'user-item';

        // 1. 뼈대 먼저 그리기 (이미지는 일단 기본값으로 설정)
        userItem.innerHTML = `
            <img src="${DEFAULT_AVATAR}" alt="${user.username}" class="user-avatar" id="avatar-${user.id}">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        fragment.appendChild(userItem);

        // 2. 프로필 ID가 있다면, 백그라운드에서 이미지 로드 시작
        if (user.profileId) {
            loadUserProfileImage(user.profileId, userItem.querySelector('.user-avatar'));
        }
    });

    userListElement.appendChild(fragment);
}

/**
 * 개별 프로필 이미지를 비동기로 가져와서 교체합니다.
 * @param {string} profileId - 프로필 이미지 ID
 * @param {HTMLImageElement} imgElement - 업데이트할 이미지 태그
 */
async function loadUserProfileImage(profileId, imgElement) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);

        if (!response.ok) return; // 실패하면 기본 이미지 유지

        const profile = await response.json();

        // Base64 데이터가 유효한지 확인 후 적용
        if (profile.contentType && profile.bytes) {
            imgElement.src = `data:${profile.contentType};base64,${profile.bytes}`;
        }
    } catch (error) {
        console.warn(`프로필 이미지 로드 실패 (ID: ${profileId})`, error);
        // 에러 발생 시 기본 이미지 유지
    }
}