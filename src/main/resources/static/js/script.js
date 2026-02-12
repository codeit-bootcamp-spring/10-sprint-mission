const ENDPOINTS = {
    USERS: "/users/api/user/findAll",
    BINARY_CONTENT: "/binary-contents/api/binaryContent/find"
};

document.addEventListener("DOMContentLoaded", () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const res = await fetch(ENDPOINTS.USERS);
        if (!res.ok) {
            const text = await safeText(res);
            throw new Error(`Failed to fetch users: ${res.status} ${text}`);
        }

        const users = await res.json();
        await renderUserList(users);
    } catch (e) {
        console.error(e);
    }
}

async function safeText(res) {
    try { return await res.text(); } catch { return ""; }
}

function svgAvatar(username = "?") {
    const initial = (username.trim()[0] || "?").toUpperCase();

    // 간단한 색상 해시
    let hash = 0;
    for (let i = 0; i < username.length; i++) hash = (hash * 31 + username.charCodeAt(i)) >>> 0;
    const hue = hash % 360;

    const svg =
        `<svg xmlns="http://www.w3.org/2000/svg" width="64" height="64">
      <rect width="100%" height="100%" rx="32" ry="32" fill="hsl(${hue},70%,50%)"/>
      <text x="50%" y="54%" text-anchor="middle" font-size="28" font-family="Arial" fill="#fff" font-weight="700">${initial}</text>
     </svg>`;

    return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`;
}

// ✅ profileId로 BinaryContent 조회 → filePath가 “실제로 접근 가능한 웹 경로”면 사용, 아니면 SVG fallback
async function fetchProfileUrl(profileId, username) {
    // 기본: 네트워크 요청 없이도 무조건 표시되게
    const fallback = svgAvatar(username);

    if (!profileId) return fallback;

    try {
        const res = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!res.ok) return fallback;

        const profile = await res.json();

        // (혹시 bytes가 나오는 구현이면) data URL로 표시
        if (profile.bytes && profile.contentType) {
            return `data:${profile.contentType};base64,${profile.bytes}`;
        }

        // 현재 너희 BinaryContent는 filePath만 있음
        if (profile.filePath) {
            // "/images/a.png" 형태면 그대로
            if (profile.filePath.startsWith("/")) return profile.filePath;
            // "images/a.png" 형태면 앞에 "/" 붙여서
            return `/${profile.filePath}`;
        }

        return fallback;
    } catch (e) {
        console.error("profile load error:", e);
        return fallback;
    }
}

async function renderUserList(users) {
    const root = document.getElementById("userList");
    if (!root) {
        console.error("Cannot find #userList container");
        return;
    }

    root.innerHTML = "";

    for (const u of users) {
        const profileUrl = await fetchProfileUrl(u.profileId, u.username);

        const row = document.createElement("div");
        row.className = "row";

        const onlineText = u.online ? "온라인" : "오프라인";
        const badgeClass = u.online ? "is-online" : "is-offline";

        row.innerHTML = `
      <div class="left">
        <img class="avatar" src="${profileUrl}" alt="profile" />
        <div class="meta">
          <div class="name">${u.username ?? ""}</div>
          <div class="email">${u.email ?? ""}</div>
        </div>
      </div>
      <div class="badge ${badgeClass}">${onlineText}</div>
    `;

        root.appendChild(row);
    }
}
