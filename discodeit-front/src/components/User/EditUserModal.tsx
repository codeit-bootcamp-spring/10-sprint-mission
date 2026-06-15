import React, {useEffect, useState} from 'react';
import styled from 'styled-components';

import defaultProfile from '@/assets/default_profile.png';
import useBinaryContentStore from '@/stores/binaryContentStore';
import {BinaryContentDto, BinaryContentStatus, UserDto, UserUpdateRequest} from '@/types/api';
import useAuthStore from '@/stores/authStore';
import useUserStore from "@/stores/userStore.ts";
import {uploadProfile} from "@/api/upload.ts";

interface EditUserModalProps {
  isOpen: boolean;
  onClose: () => void;
  user: UserDto;
}

function EditUserModal({isOpen, onClose, user}: EditUserModalProps): JSX.Element | null {
  const [username, setUsername] = useState(user.username);
  const [email, setEmail] = useState(user.email);
  const [password, setPassword] = useState('');
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [error, setError] = useState('');
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const {binaryContents, fetchBinaryContent} = useBinaryContentStore();
  const {logout} = useAuthStore();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user.profile?.id && !binaryContents[user.profile.id]) {
      fetchBinaryContent(user.profile.id);
    }
  }, [user.profile, binaryContents, fetchBinaryContent]);

  const handleClose = () => {
    setUsername(user.username);
    setEmail(user.email);
    setPassword('');
    setProfileImage(null);
    setPreviewUrl(null);
    setError('');
    onClose();
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setProfileImage(file);
      // 이미지 미리보기 생성
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isLoading) return; // 중복 클릭 방지
    setError('');

    // 🌟 변경된 사항이 무엇인지 정확히 분류합니다.
    const isTextChanged = username !== user.username || email !== user.email || !!password;
    const isImageChanged = !!profileImage;

    // 변경사항이 아예 없으면 그냥 닫습니다.
    if (!isTextChanged && !isImageChanged) {
      onClose();
      return;
    }

    // =====================================================================
    // 🚀 트랙 1: 오직 '프로필 이미지'만 변경한 경우 (UX 극대화 / 낙관적 업데이트)
    // =====================================================================
    if (!isTextChanged && isImageChanged) {
      const localPreview = previewUrl;
      const tempProfileId = 'temp-local-profile';

      // 1. UI 즉각 반영 (가짜 데이터 밀어넣기)
      if (localPreview) {
        useBinaryContentStore.setState((state) => ({
          binaryContents: {
            ...state.binaryContents,
            [tempProfileId]: {
              url: localPreview,
              contentType: '',
              fileName: '',
              size: 0,
              status: BinaryContentStatus.SUCCESS,
              revokeUrl: () => URL.revokeObjectURL(localPreview)
            }
          }
        }));
        useAuthStore.getState().updateCurrentUser({
          ...user,
          profile: { id: tempProfileId } as unknown as BinaryContentDto
        });
      }

      // 2. 모달 즉시 닫기 (유저 대기 시간 0초)
      onClose();

      // 3. 백그라운드에서 조용히 업로드 및 서버 동기화
      (async () => {
        try {
          const profileDto = await uploadProfile(profileImage);
          const updatedUser = await useUserStore.getState().updateUser(user.id, { newProfileId: profileDto.id });
          useAuthStore.getState().updateCurrentUser(updatedUser); // 진짜 데이터로 교체
        } catch (err) {
          console.error('백그라운드 프로필 업데이트 실패:', err);
        }
      })();

      return; // 여기서 함수를 종료합니다.
    }

    // =====================================================================
    // 🛡️ 트랙 2: 텍스트(이름, 이메일 등)가 변경된 경우 (정합성 극대화 / 백엔드 검증)
    // =====================================================================
    try {
      setIsLoading(true); // 버튼 비활성화 및 로딩 스피너 작동
      let finalProfileId: string | undefined = undefined;

      // 이미지가 함께 변경되었다면 업로드 먼저 수행
      if (isImageChanged && profileImage) {
        const profileDto = await uploadProfile(profileImage);
        finalProfileId = profileDto.id;
      }

      // 텍스트 업데이트 객체 조립
      const updateRequest: UserUpdateRequest = {};
      if (username !== user.username) updateRequest.newUsername = username;
      if (email !== user.email) updateRequest.newEmail = email;
      if (password) updateRequest.newPassword = password;
      if (finalProfileId) updateRequest.newProfileId = finalProfileId;

      // 🌟 백엔드 서버의 검증 로직이 끝날 때까지 얌전히 기다립니다 (await)
      const updatedUser = await useUserStore.getState().updateUser(user.id, updateRequest);
      useAuthStore.getState().updateCurrentUser(updatedUser);

      onClose(); // 성공적으로 갱신되었을 때만 모달을 닫습니다.

    } catch (error: any) {
      console.error(error);
      setError('이미 사용 중인 이메일이거나, 수정에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
      <StyledModal>
        <ModalContent>
          <h2>프로필 수정</h2>
          <form onSubmit={handleSubmit}>
            <FormField>
              <Label>프로필 이미지</Label>
              <ImageContainer>
                <ProfileImage
                    src={previewUrl || (user.profile?.id ? binaryContents[user.profile.id]?.url : undefined) || defaultProfile}
                    alt="profile"
                />
                <ImageInput
                    type="file"
                    accept="image/*"
                    onChange={handleImageChange}
                    id="profile-image"
                />
                <ImageLabel htmlFor="profile-image">
                  이미지 변경
                </ImageLabel>
              </ImageContainer>
            </FormField>
            <FormField>
              <Label>사용자명 <Required>*</Required></Label>
              <Input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
              />
            </FormField>
            <FormField>
              <Label>이메일 <Required>*</Required></Label>
              <Input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
              />
            </FormField>
            <FormField>
              <Label>새 비밀번호</Label>
              <Input
                  type="password"
                  placeholder="변경하지 않으려면 비워두세요"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
              />
            </FormField>
            {error && <ErrorMessage>{error}</ErrorMessage>}
            <ButtonGroup>
              <Button type="button" onClick={handleClose} $secondary>취소</Button>
              <Button type="submit">저장</Button>
            </ButtonGroup>
          </form>
          <LogoutButton onClick={logout}>로그아웃</LogoutButton>
        </ModalContent>
      </StyledModal>
  );
}

const StyledModal = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`;

const ModalContent = styled.div`
  background: ${({theme}) => theme.colors.background.secondary};
  padding: 32px;
  border-radius: 5px;
  width: 100%;
  max-width: 480px;

  h2 {
    color: ${({theme}) => theme.colors.text.primary};
    margin-bottom: 24px;
    text-align: center;
    font-size: 24px;
  }
`;

const Input = styled.input`
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 4px;
  background: ${({theme}) => theme.colors.background.input};
  color: ${({theme}) => theme.colors.text.primary};

  &::placeholder {
    color: ${({theme}) => theme.colors.text.muted};
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px ${({theme}) => theme.colors.brand.primary};
  }
`;

interface ButtonProps {
  $secondary?: boolean;
}

const Button = styled.button<ButtonProps>`
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background: ${({$secondary, theme}) =>
      $secondary ? 'transparent' : theme.colors.brand.primary};
  color: ${({theme}) => theme.colors.text.primary};
  cursor: pointer;
  font-weight: 500;

  &:hover {
    background: ${({$secondary, theme}) =>
        $secondary ? theme.colors.background.hover : theme.colors.brand.hover};
  }
`;

const ErrorMessage = styled.div`
  color: ${({theme}) => theme.colors.status.error};
  font-size: 14px;
  margin-bottom: 10px;
`;

const ImageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
`;

const ProfileImage = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 10px;
  object-fit: cover;
`;

const ImageInput = styled.input`
  display: none;
`;

const ImageLabel = styled.label`
  color: ${({theme}) => theme.colors.brand.primary};
  cursor: pointer;
  font-size: 14px;

  &:hover {
    text-decoration: underline;
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 10px;
  margin-top: 20px;
`;

const LogoutButton = styled.button`
  width: 100%;
  padding: 10px;
  margin-top: 16px;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: ${({theme}) => theme.colors.status.error};
  cursor: pointer;
  font-weight: 500;

  &:hover {
    background: ${({theme}) => theme.colors.status.error}20;
  }
`;

const FormField = styled.div`
  margin-bottom: 20px;
`;

const Label = styled.label`
  display: block;
  color: ${({theme}) => theme.colors.text.muted};
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 8px;
`;

const Required = styled.span`
  color: ${({theme}) => theme.colors.status.error};
`;

export default EditUserModal; 
