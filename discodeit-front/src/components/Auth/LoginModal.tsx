import React, {useCallback, useState} from 'react'
import useUserListStore from '../../stores/userListStore'
import useAuthStore from '../../stores/authStore'
import SignUpModal from './SignUpModal'
import {
  Button,
  Checkbox,
  ErrorMessage,
  Input,
  ModalContent,
  SignUpLink,
  SignUpText,
  StyledLoginModal
} from './styles'
import styled from 'styled-components'

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose }) => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSignUpOpen, setIsSignUpOpen] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  
  const { login } = useAuthStore();
  const { fetchUsers } = useUserListStore();

  const clearForm = useCallback(() => {
    // 컴포넌트 언마운트 시 상태 초기화
    setUsernameOrEmail('');
    setPassword('');
    setError('');
    setRememberMe(false);
    setIsSignUpOpen(false);
  }, []);

  const handleSignUpOpen = useCallback(() => {
    clearForm();
    setIsSignUpOpen(true)
  }, [clearForm, onClose]);

  const handleLogin = async () => {
    try {
      await login(usernameOrEmail, password, rememberMe);
      await fetchUsers();
      clearForm();
      onClose();
    } catch (error: any) {
      console.error('로그인 에러:', error);
      if (error.response?.status === 401) {
        setError('아이디 또는 비밀번호가 올바르지 않습니다.');
      } else {
        setError('로그인에 실패했습니다.');
      }
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <StyledLoginModal>
        <ModalContent>
          <h2>돌아오신 것을 환영해요!</h2>
          <form onSubmit={(e) => {
            e.preventDefault();
            handleLogin();
          }}>
            <Input
              type="text"
              placeholder="사용자 이름"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
            />
            <Input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <RememberMeContainer>
             <Checkbox
                id="rememberMe"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              />
              <RememberMeLabel htmlFor="rememberMe">로그인 유지</RememberMeLabel>
            </RememberMeContainer>
            {error && <ErrorMessage>{error}</ErrorMessage>}
            <Button type="submit">로그인</Button>
          </form>
          <SignUpText>
            계정이 필요한가요? <SignUpLink onClick={handleSignUpOpen}>가입하기</SignUpLink>
          </SignUpText>
        </ModalContent>
      </StyledLoginModal>
      <SignUpModal 
        isOpen={isSignUpOpen}
        onClose={() => setIsSignUpOpen(false)}
      />
    </>
  );
};

export default LoginModal; 


const RememberMeContainer = styled.div`
  display: flex;
  align-items: center;
  margin: 10px 0;
  justify-content: flex-start;
`;

const RememberMeLabel = styled.label`
  margin-left: 8px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  text-align: left;
`;