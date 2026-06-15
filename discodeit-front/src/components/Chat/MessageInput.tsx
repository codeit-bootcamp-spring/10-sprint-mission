import useMessageStore from '@/stores/messageStore';
import {BinaryContentDto, ChannelDto} from '@/types/api';
import React, {useEffect, useRef, useState} from 'react';
import {
  AttachButton,
  AttachmentPreviewItem,
  AttachmentPreviewList,
  ImagePreviewItem,
  Input,
  LoadingSpinner,
  PreviewFileIcon,
  PreviewFileName,
  RemoveButton,
  StyledMessageInput
} from './styles';
import useAuthStore from '@/stores/authStore';
import {uploadFiles} from "@/api/upload.ts";

interface MessageInputProps {
  channel: ChannelDto;
}

function MessageInput({channel}: MessageInputProps): JSX.Element | null {
  const [content, setContent] = useState('');
  const [attachments, setAttachments] = useState<File[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const createMessage = useMessageStore((state) => state.createMessage);
  const {currentUser} = useAuthStore();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!content.trim() && attachments.length === 0) return;
    if (isLoading) return;

    setIsLoading(true);

    try {
      let attachmentIds: string[] = [];
      let result: BinaryContentDto[] = [];

      if (attachments.length > 0) {
        result = await uploadFiles(attachments);
        attachmentIds = result.map(dto => dto.id);
      }

      await createMessage({
        content: content.trim(),
        channelId: channel.id,
        authorId: currentUser?.id ?? '',
        attachmentIds: attachmentIds.length > 0 ? attachmentIds : undefined,
      });

      setContent('');
      setAttachments([]);
    } catch (error) {
      console.error('메시지 전송 실패:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []).map(file => {
      if (file.type.startsWith('image/')) {
        (file as any).previewUrl = URL.createObjectURL(file);
      }
      return file;
    });
    setAttachments(prev => [...prev, ...files]);
    e.target.value = '';
  };

  const removeAttachment = (index: number) => {
    const targetFile = attachments[index];
    if (targetFile && (targetFile as any).previewUrl) {
      URL.revokeObjectURL((targetFile as any).previewUrl);
    }
    setAttachments(prev => prev.filter((_, i) => i !== index));
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      console.log('Enter key pressed');
      e.preventDefault();

      // 한글 입력 중인지 확인
      // composition 이벤트가 진행 중인지 확인하는 flag 추가
      if (e.nativeEvent.isComposing) {
        return;
      }

      handleSubmit(e as unknown as React.FormEvent<HTMLFormElement>);
    }
  };

  const renderPreview = (file: any, index: number) => {
    if (file.type.startsWith('image/')) {
      return (
          <ImagePreviewItem key={index}>
            {/* 🌟 매번 새로 생성하지 않고, 파일이 들고 있는 URL을 그대로 꽂아줍니다. */}
            <img src={file.previewUrl} alt={file.name}/>
            <RemoveButton onClick={() => removeAttachment(index)}>×</RemoveButton>
          </ImagePreviewItem>
      );
    }

    return (
        <AttachmentPreviewItem key={index}>
          <PreviewFileIcon>📎</PreviewFileIcon>
          <PreviewFileName>{file.name}</PreviewFileName>
          <RemoveButton onClick={() => removeAttachment(index)}>×</RemoveButton>
        </AttachmentPreviewItem>
    );
  };

  const attachmentsRef = useRef(attachments);
  attachmentsRef.current = attachments;
  useEffect(() => {
    return () => {
      attachmentsRef.current.forEach(file => {
        if ((file as any).previewUrl) {
          URL.revokeObjectURL((file as any).previewUrl);
        }
      });
    };
  }, []);

  // cleanup URLs when attachments change
  useEffect(() => {
    return () => {
      attachments.forEach(file => {
        if (file.type.startsWith('image/')) {
          URL.revokeObjectURL(URL.createObjectURL(file));
        }
      });
    };
  }, [attachments]);

  if (!channel) return null;

  return (
      <>
        {attachments.length > 0 && !isLoading && (
            <AttachmentPreviewList>
              {attachments.map((file, index) => renderPreview(file, index))}
            </AttachmentPreviewList>
        )}
        <StyledMessageInput onSubmit={handleSubmit}>
          <AttachButton as="label">
            +
            <input
                type="file"
                multiple
                onChange={handleFileChange}
                style={{display: 'none'}}
            />
          </AttachButton>
          <Input
              value={content}
              onChange={(e) => setContent(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={isLoading}
              placeholder={
                isLoading
                    ? '메시지 전송 중...'
                    : channel.type === 'PUBLIC'
                        ? `#${channel.name}에 메시지 보내기`
                        : '메시지 보내기'
              }
          />
          {isLoading && <LoadingSpinner/>}
        </StyledMessageInput>
      </>
  );
}

export default MessageInput; 
