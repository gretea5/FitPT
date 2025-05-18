import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import emailjs from 'emailjs-com';
import '../styles/Modal.css'; // 모달 스타일

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  overlayClose?: boolean;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, overlayClose = true }) => {
  const [formData, setFormData] = useState({
    name: '',
    center: '',
    email: '',
    phone: '',
    agree: false,
  });

  if (!isOpen) return null;

  const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (overlayClose && e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData({ ...formData, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = () => {
    if (!formData.agree) return alert('개인정보 수집 및 이용 동의를 해주세요.');
    if (!formData.name || !formData.center || !formData.email || !formData.phone) {
      return alert('모든 항목을 입력해주세요.');
    }

    const templateParams = {
      from_name: formData.name,
      center: formData.center,
      from_email: formData.email,
      phone: formData.phone,
      email: formData.email,
    };

    emailjs.send(
      import.meta.env.VITE_EMAILJS_SERVICE_ID!,
      import.meta.env.VITE_EMAILJS_TEMPLATE_ID_ADMIN!,
      templateParams,
      import.meta.env.VITE_EMAILJS_PUBLIC_KEY!
    ).catch((error) => {
      console.error('관리자 메일 전송 실패:', error);
    });

    emailjs.send(
      import.meta.env.VITE_EMAILJS_SERVICE_ID!,
      import.meta.env.VITE_EMAILJS_TEMPLATE_ID_USER!,
      templateParams,
      import.meta.env.VITE_EMAILJS_PUBLIC_KEY!
    ).then(() => {
      alert('신청이 완료되었습니다.');
      onClose();
    }).catch((error) => {
      console.error('사용자 메일 전송 실패:', error);
      alert('메일 전송 중 오류가 발생했습니다.');
    });
  };

  const modalContent = (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>핏피티 체험 신청하기</h3>
        <p>핏피티 솔루션 체험 계정을 발급해드립니다.</p>
        <div className="modal-inputs">
          <input type="text" name="name" placeholder="이름" onChange={handleChange} />
          <input type="text" name="center" placeholder="센터명" onChange={handleChange} />
          <input type="email" name="email" placeholder="이메일" onChange={handleChange} />
          <input type="tel" name="phone" placeholder="연락처" onChange={handleChange} />
        </div>
        <label>
          <input type="checkbox" name="agree" onChange={handleChange} />
          개인정보 수집 및 이용 동의
        </label>
        <p className="modal-note">* 관리자 확인 후 연락드리겠습니다.</p>
        <div className="modal-buttons">
          <button onClick={handleSubmit}>신청하기</button>
          <button onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );

  return ReactDOM.createPortal(modalContent, document.body);
};

export default Modal;
