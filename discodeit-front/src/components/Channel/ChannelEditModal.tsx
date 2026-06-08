import React, { useState, useEffect } from 'react';
import useChannelStore from '@/stores/channelStore';
import {
  CloseButton,
  CreateButton,
  Description,
  ErrorMessage,
  Form,
  Input,
  InputGroup,
  Label,
  ModalContainer,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  ModalTitle,
} from './styles';
import { ChannelDto, PublicChannelUpdateRequest } from '@/types/api';

interface ChannelEditModalProps {
  isOpen: boolean;
  channel: ChannelDto | null;
  onClose: () => void;
  onUpdateSuccess: (channelData: ChannelDto) => void;
}

interface ChannelFormData {
  name: string;
  description: string;
}

function ChannelEditModal({ isOpen, channel, onClose, onUpdateSuccess }: ChannelEditModalProps): JSX.Element | null {
  const [channelData, setChannelData] = useState<ChannelFormData>({
    name: '',
    description: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { updatePublicChannel } = useChannelStore();

  // Initialize form data when channel changes
  useEffect(() => {
    if (channel && isOpen) {
      setChannelData({
        name: channel.name || '',
        description: channel.description || ''
      });
      setError('');
    }
  }, [channel, isOpen]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setChannelData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!channel) return;

    setError('');
    setLoading(true);

    try {
      if (!channelData.name.trim()) {
        setError('채널 이름을 입력해주세요.');
        setLoading(false);
        return;
      }

      const updateData: PublicChannelUpdateRequest = {
        newName: channelData.name.trim(),
        newDescription: channelData.description.trim()
      };

      const updatedChannel = await updatePublicChannel(channel.id, updateData);
      onUpdateSuccess(updatedChannel);
    } catch (error: any) {
      console.error('채널 수정 실패:', error);
      setError(
        error.response?.data?.message || 
        '채널 수정에 실패했습니다. 다시 시도해주세요.'
      );
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !channel) return null;

  // Only PUBLIC channels can be edited
  if (channel.type !== 'PUBLIC') return null;

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContainer onClick={e => e.stopPropagation()}>
        <ModalHeader>
          <ModalTitle>채널 수정</ModalTitle>
          <CloseButton onClick={onClose}>&times;</CloseButton>
        </ModalHeader>
        <ModalContent>
          <Form onSubmit={handleSubmit}>
            {error && <ErrorMessage>{error}</ErrorMessage>}
            <InputGroup>
              <Label>채널 이름</Label>
              <Input
                name="name"
                value={channelData.name}
                onChange={handleChange}
                placeholder="새로운-채널"
                required
                disabled={loading}
              />
            </InputGroup>
            <InputGroup>
              <Label>채널 설명</Label>
              <Description>
                이 채널의 주제를 설명해주세요.
              </Description>
              <Input
                name="description"
                value={channelData.description}
                onChange={handleChange}
                placeholder="채널 설명을 입력하세요"
                disabled={loading}
              />
            </InputGroup>
            <CreateButton type="submit" disabled={loading}>
              {loading ? '수정 중...' : '채널 수정'}
            </CreateButton>
          </Form>
        </ModalContent>
      </ModalContainer>
    </ModalOverlay>
  );
}

export default ChannelEditModal;