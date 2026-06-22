import useUserListStore from "@/stores/userListStore.ts";
import defaultProfile from '@/assets/default_profile.png';
import useBinaryContentStore, {BinaryContentInfo} from '@/stores/binaryContentStore';
import useMessageStore from '@/stores/messageStore';
import useAuthStore from '@/stores/authStore';
import {BinaryContentDto, BinaryContentStatus, ChannelDto, MessageDto} from '@/types/api';
import {useCallback, useEffect, useState} from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import {Avatar} from '@/styles/common.ts';
import {
  AttachmentList,
  AuthorAvatarContainer,
  FileIcon,
  FileInfo,
  FileItem,
  FileName,
  FileSize,
  ImagePreview,
  MessageAuthor,
  MessageContent,
  MessageEditButton,
  MessageEditButtons,
  MessageEditContainer,
  MessageEditTextarea,
  MessageHeader,
  MessageItem,
  MessageListWrapper,
  MessageMenuButton,
  MessageMenuContainer,
  MessageMenuDropdown,
  MessageMenuItem,
  MessageTime,
  StyledMessageList
} from './styles';
import {eventEmitter} from "@/utils/eventEmitter.ts";
import WebSocket from "@/components/WebSocket/WebSocket.tsx";
import {WS_DESTINATIONS} from '@/constants/websocket';

interface MessageListProps {
  channel: ChannelDto;
}

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B';
  else if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
  else if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  else return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
};

function MessageList({channel}: MessageListProps): JSX.Element {
  const {
    messages,
    fetchMessages,
    loadMoreMessages,
    pagination,
    updateMessage,
    deleteMessage,
    newMessages,
    addNewMessage,
    updateMessageLocally,
    deleteMessageLocally,
    clear: clearMessages
  } = useMessageStore();
  const {
    binaryContents,
    fetchBinaryContents,
    clearBinaryContents
  } = useBinaryContentStore();
  const {currentUser} = useAuthStore();
  const {users} = useUserListStore();
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [editingMessageId, setEditingMessageId] = useState<string | null>(null);
  const [editingContent, setEditingContent] = useState<string>('');

  const handleReceiveWebSocketMessage = useCallback((payload: any, type: string = 'CREATED') => {
    const handlers: Record<string, () => void> = {
      'CREATED': () => {
        const authorId = payload.authorId;
        let author = channel.participants?.find(p => p.id === authorId)
            || users.find(u => u.id === authorId);
        if (!author) {
          author = {
            id: authorId,
            username: '알 수 없음',
            email: '',
            online: false,
            role: 'USER' as any
          };
        }
        const newMessage: MessageDto = {
          id: payload.id,
          createdAt: payload.createdAt,
          updatedAt: payload.createdAt,
          content: payload.content,
          channelId: channel.id,
          author: author,
          attachments: payload.attachmentIds?.map((id: string) => ({ id } as BinaryContentDto)) || []
        };
        addNewMessage(newMessage);
      },
      'UPDATED': () => {
        updateMessageLocally(payload.id, payload);
      },
      'DELETED': () => {
        deleteMessageLocally(payload.id);
      }
    };

    const handler = handlers[type];
    if (handler) {
      handler();
    } else {
      console.warn('알 수 없는 메시지 타입입니다:', type);
    }
  }, [channel, users, addNewMessage, updateMessageLocally, deleteMessageLocally]);

  useEffect(() => {
    if (channel?.id) {
      fetchMessages(channel.id, null);

      return () => {
        clearMessages();
        const toRevokeContentIds = messages
        .map(message => message.attachments?.map(attachment => attachment.id)).flat();
        clearBinaryContents(toRevokeContentIds);
      };
    }
  }, [channel?.id, fetchMessages, clearMessages]);

  useEffect(() => {
    const missingIds: string[] = [];
    [...messages, ...newMessages].forEach(message => {
      message.attachments?.forEach(attachment => {
        if (!binaryContents[attachment.id]) {
          missingIds.push(attachment.id);
        }
      });
    });
    if (missingIds.length > 0) {
      fetchBinaryContents(missingIds);
    }
  }, [messages, newMessages, fetchBinaryContents]);

  useEffect(() => {
    const handleClickOutside = () => {
      if (openMenuId) {
        setOpenMenuId(null);
      }
    };

    if (openMenuId) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [openMenuId]);

  const handleDownload = async (attachment: BinaryContentInfo) => {
    try {
      const {url, fileName} = attachment;
      if (url == undefined) return
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;

      link.style.display = 'none';
      document.body.appendChild(link);

      // showSaveFilePicker API를 사용하여 저장 경로 선택 다이얼로그 표시
      try {
        const handle = await (window as any).showSaveFilePicker({
          suggestedName: attachment.fileName,
          types: [{
            description: 'Files',
            accept: {
              '*/*': ['.txt', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.jpg', '.jpeg', '.png', '.gif']
            }
          }]
        });

        const writable = await handle.createWritable();
        const response = await fetch(url);
        const blob = await response.blob();
        await writable.write(blob);
        await writable.close();
      } catch (err: any) {
        // 사용자가 취소하거나 브라우저가 API를 지원하지 않는 경우
        // 기본 다운로드 방식으로 폴백
        if (err.name !== 'AbortError') {
          link.click();
        }
      }

      // cleanup
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('파일 다운로드 실패:', error);
    }
  };

  const renderAttachments = (attachments?: BinaryContentDto[]) => {
    if (!attachments?.length) return null;

    return attachments.map((_attachment) => {
      const attachment = binaryContents[_attachment.id];
      if (!attachment) return null;

      const isImage = attachment.contentType.startsWith('image/');
      const status = attachment.status; // binaryContents에서 가져온 최신 상태 사용
      // 업로드 실패한 파일 처리
      if (status === BinaryContentStatus.FAILED) {
        return (
            <AttachmentList key={_attachment.id}>
              <FileItem
                  href="#"
                  style={{opacity: 0.5, backgroundColor: '#fff2f2'}}
                  onClick={(e) => {
                    e.preventDefault();
                  }}
              >
                <FileIcon>
                  <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
                    <path
                        d="M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z"
                        fill="#ef4444" fillOpacity="0.1"/>
                    <path d="M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z" fill="#ef4444"
                          fillOpacity="0.3"/>
                    <path d="M13 19H27M13 25H27M13 31H27" stroke="#ef4444" strokeWidth="2"
                          strokeLinecap="round"/>
                  </svg>
                </FileIcon>
                <FileInfo>
                  <FileName style={{color: '#ef4444'}}>{_attachment.fileName}</FileName>
                  <FileSize style={{color: '#ef4444'}}>업로드 실패</FileSize>
                </FileInfo>
              </FileItem>
            </AttachmentList>
        );
      }

      // 업로드 중인 파일 처리
      if (status === 'PROCESSING') {
        return (
            <AttachmentList key={_attachment.id}>
              <FileItem
                  href="#"
                  style={{opacity: 0.7, backgroundColor: '#fef3c7'}}
                  onClick={(e) => {
                    e.preventDefault();
                  }}
              >
                <FileIcon>
                  <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
                    <path
                        d="M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z"
                        fill="#f59e0b" fillOpacity="0.1"/>
                    <path d="M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z" fill="#f59e0b"
                          fillOpacity="0.3"/>
                    <path d="M13 19H27M13 25H27M13 31H27" stroke="#f59e0b" strokeWidth="2"
                          strokeLinecap="round"/>
                  </svg>
                </FileIcon>
                <FileInfo>
                  <FileName style={{color: '#f59e0b'}}>{_attachment.fileName}</FileName>
                  <FileSize style={{color: '#f59e0b'}}>업로드 중...</FileSize>
                </FileInfo>
              </FileItem>
            </AttachmentList>
        );
      }

      // 성공한 파일 처리 (url이 있는 경우에만)
      if (attachment.url) {
        if (isImage) {
          return (
              <AttachmentList key={attachment.url}>
                <ImagePreview
                    href="#"
                    onClick={(e) => {
                      e.preventDefault();
                      handleDownload(attachment);
                    }}
                >
                  <img
                      src={attachment.url}
                      alt={attachment.fileName}
                  />
                </ImagePreview>
              </AttachmentList>
          );
        }

        return (
            <AttachmentList key={attachment.url}>
              <FileItem
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    handleDownload(attachment);
                  }}
              >
                <FileIcon>
                  <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
                    <path
                        d="M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z"
                        fill="#0B93F6" fillOpacity="0.1"/>
                    <path d="M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z" fill="#0B93F6"
                          fillOpacity="0.3"/>
                    <path d="M13 19H27M13 25H27M13 31H27" stroke="#0B93F6" strokeWidth="2"
                          strokeLinecap="round"/>
                  </svg>
                </FileIcon>
                <FileInfo>
                  <FileName>{attachment.fileName}</FileName>
                  <FileSize>{formatFileSize(attachment.size)}</FileSize>
                </FileInfo>
              </FileItem>
            </AttachmentList>
        );
      }

      // url이 없는 경우 null 반환 (PROCESSING이면서 아직 다운로드되지 않은 상태)
      return null;
    });
  };

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString();
  };

  const fetchMoreData = () => {
    if (channel?.id) {
      loadMoreMessages(channel.id);
    }
  };

  const handleMenuToggle = (messageId: string) => {
    setOpenMenuId(openMenuId === messageId ? null : messageId);
  };

  const handleEditMessage = (messageId: string) => {
    setOpenMenuId(null);
    const message = messages.find(m => m.id === messageId);
    if (message) {
      setEditingMessageId(messageId);
      setEditingContent(message.content);
    }
  };

  const handleSaveEdit = (messageId: string) => {
    updateMessage(messageId, editingContent)
    .catch((error) => {
      console.error('메시지 수정 실패:', error);
      eventEmitter.emit('api-error', {error, alert: true});
    });
    setEditingMessageId(null);
    setEditingContent('');
  };

  const handleCancelEdit = () => {
    setEditingMessageId(null);
    setEditingContent('');
  };

  const handleDeleteMessage = (messageId: string) => {
    setOpenMenuId(null);
    deleteMessage(messageId)
  };

  return (
      <MessageListWrapper>
        <div id="scrollableDiv" style={{
          height: '100%',
          overflow: 'auto',
          display: 'flex',
          flexDirection: 'column-reverse'
        }}>
          <InfiniteScroll
              dataLength={messages.length}
              next={fetchMoreData}
              hasMore={pagination.hasNext}
              loader={<h4 style={{textAlign: 'center'}}>메시지를 불러오는 중...</h4>}
              scrollableTarget="scrollableDiv"
              style={{display: 'flex', flexDirection: 'column-reverse'}}
              inverse={true}
              endMessage={
                <p style={{textAlign: 'center'}}>
                  <b>{pagination.nextCursor !== null ? "모든 메시지를 불러왔습니다" : ""}</b>
                </p>
              }
          >
            <StyledMessageList>
              {[...[...messages].reverse(), ...newMessages].map(message => {
                const author = message.author;
                const isOwnMessage = currentUser && author && author.id === currentUser.id;

                return (
                    <MessageItem key={message.id}>
                      <AuthorAvatarContainer>
                        <Avatar
                            src={author && author.profile ? binaryContents[author.profile.id]?.url : defaultProfile}
                            alt={author && author.username || '알 수 없음'}
                        />
                      </AuthorAvatarContainer>
                      <div>
                        <MessageHeader>
                          <MessageAuthor>{author && author.username || '알 수 없음'}</MessageAuthor>
                          <MessageTime>
                            {formatTime(message.createdAt)}
                          </MessageTime>
                          {isOwnMessage && (
                              <MessageMenuContainer>
                                <MessageMenuButton
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      handleMenuToggle(message.id);
                                    }}
                                >
                                  ⋯
                                </MessageMenuButton>
                                {openMenuId === message.id && (
                                    <MessageMenuDropdown onClick={(e) => e.stopPropagation()}>
                                      <MessageMenuItem
                                          onClick={() => handleEditMessage(message.id)}
                                      >
                                        ✏️ 수정
                                      </MessageMenuItem>
                                      <MessageMenuItem
                                          onClick={() => handleDeleteMessage(message.id)}
                                      >
                                        🗑️ 삭제
                                      </MessageMenuItem>
                                    </MessageMenuDropdown>
                                )}
                              </MessageMenuContainer>
                          )}
                        </MessageHeader>
                        {editingMessageId === message.id ? (
                            <MessageEditContainer>
                              <MessageEditTextarea
                                  value={editingContent}
                                  onChange={(e) => setEditingContent(e.target.value)}
                                  onKeyDown={(e) => {
                                    if (e.key === 'Escape') {
                                      handleCancelEdit();
                                    } else if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
                                      e.preventDefault();
                                      handleSaveEdit(message.id);
                                    }
                                  }}
                                  placeholder="메시지를 입력하세요..."
                              />
                              <MessageEditButtons>
                                <MessageEditButton
                                    variant="secondary"
                                    onClick={handleCancelEdit}
                                >
                                  취소
                                </MessageEditButton>
                                <MessageEditButton
                                    variant="primary"
                                    onClick={() => handleSaveEdit(message.id)}
                                >
                                  저장
                                </MessageEditButton>
                              </MessageEditButtons>
                            </MessageEditContainer>
                        ) : (
                            <MessageContent>{message.content}</MessageContent>
                        )}
                        {renderAttachments(message.attachments)}
                      </div>
                    </MessageItem>
                );
              })}
            </StyledMessageList>
          </InfiniteScroll>
        </div>
        <WebSocket
            destination={WS_DESTINATIONS.SUB_MESSAGE(channel.id)}
            subscribeCallback={handleReceiveWebSocketMessage}
        />
      </MessageListWrapper>
  );
}

export default MessageList;
