import "../styles/Footer.css";
import { useState } from "react";
import Modal from "./Modal.tsx";

interface Props {
  onDemoClick: () => void;
}


const Footer: React.FC<Props> = ({onDemoClick}) => {
  const [showDemoModal, setShowDemoModal] = useState(false);

  const showKakaoModal = () => {
    window.open(import.meta.env.VITE_KAKAO_CHANNEL_URL, "_blank");
  };

  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-logo">
            <span className="logo-text-footer">핏피티</span>
            <p className="footer-description">
              헬스장, 건강 관리의 새로운 기준,
              <br />
              핏피티로 시작하는 스마트 피트니스 솔루션
            </p>
          </div>

          <div className="footer-links">
            <div className="footer-links-column">
              <h4 className="footer-links-title">서비스</h4>
              <ul className="footer-links-list">
                <li>
                  <a href="#features">주요 기능</a>
                </li>
                <li>
                  <a href="#how-it-works">이용 방법</a>
                </li>
              </ul>
            </div>

            <div className="footer-links-column">
              <h4 className="footer-links-title">핏피티</h4>
              <ul className="footer-links-list">
                <li>
                  <button onClick={onDemoClick} className="footer-modal-link">
                    핏피티 데모 신청
                  </button>
                </li>
                <li>
                  <button onClick={showKakaoModal} className="footer-modal-link">
                    카카오톡 상담하기
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div className="footer-bottom">
          <p className="copyright">© 2025 핏티티(FitPT). All rights reserved.</p>
        </div>
      </div>

      {/* Demo Modal */}
      <Modal isOpen={showDemoModal} onClose={() => setShowDemoModal(false)} overlayClose={true} />
    </footer>
  );
};

export default Footer;
