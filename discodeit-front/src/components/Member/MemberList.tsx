import WebSocket from '@/components/WebSocket/WebSocket';
import {WS_DESTINATIONS} from '@/constants/websocket';
import useUserListStore from '@/stores/userListStore';
import {UserDto} from '@/types/api';
import {useCallback, useEffect, useState} from 'react';
import MemberItem from './MemberItem';
import {MemberHeader, MemberHeaderContent, StyledMemberList} from './styles';
import useAuthStore from '@/stores/authStore';
import MemberDetailModal from './MemberDetailModal';
import NotificationIcon from '../Notification/NotificationIcon';


function MemberList(): JSX.Element {
  const users = useUserListStore((state) => state.users);
  const replaceUser = useUserListStore((state) => state.replaceUser);
  const removeUser = useUserListStore((state) => state.removeUser);
  const fetchUsers = useUserListStore((state) => state.fetchUsers);
  const {currentUser} = useAuthStore();
  const [selectedMember, setSelectedMember] = useState<UserDto | null>(null);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  // 멤버 정렬: 내 계정 > 온라인 상태 > 사용자명
  const sortedUsers = [...users].sort((a: UserDto, b: UserDto) => {
    // 내 계정을 최상단에 배치
    if (a.id === currentUser?.id) return -1;
    if (b.id === currentUser?.id) return 1;

    // 온라인 상태로 정렬
    if (a.online && !b.online) return -1;
    if (!a.online && b.online) return 1;

    // 사용자명으로 정렬
    return a.username.localeCompare(b.username);
  });

  const handleUserEvent = useCallback((message: any) => {
    // STOMP로 오는 메시지의 포맷(type)에 따라 분기 처리
    const {type, data} = message;

    // 객체 매핑(Object Mapping) 패턴 적용
    const eventHandlers: Record<string, () => void> = {
      'users.created': () => replaceUser(data as UserDto),
      'users.updated': () => replaceUser(data as UserDto),
      'users.deleted': () => removeUser((data as UserDto).id),
    };

    const handler = eventHandlers[type];

    if (handler) {
      handler();
    } else if (data && data.id) {
      // 혹시 타입 명시 없이 DTO만 바로 날아오는 경우를 대비한 방어 코드
      replaceUser(data as UserDto);
    }
  }, [replaceUser, removeUser]);

  return (
      <StyledMemberList>
        <WebSocket
            destination={WS_DESTINATIONS.SUB_ALL_USER}
            subscribeCallback={handleUserEvent}
        />
        <MemberHeader>
          <MemberHeaderContent>
            멤버 목록 - {users.length}
            <NotificationIcon/>
          </MemberHeaderContent>
        </MemberHeader>
        {sortedUsers.map(user => (
            <div key={user.id} onClick={() => setSelectedMember(user)}>
              <MemberItem key={user.id} member={user}/>
            </div>

        ))}
        {
            selectedMember && (
                <MemberDetailModal
                    member={selectedMember}
                    onClose={() => setSelectedMember(null)}
                />
            )}
      </StyledMemberList>
  );
}

export default MemberList; 
