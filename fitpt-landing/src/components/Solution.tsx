import fitpt_logo from "../assets/fitpt_logo.png";
import { useEffect, useState } from "react";
import { initScrollAnimations } from "../utils/animations";
import "../styles/Solution.css";
import Modal from "./Modal";

interface Props {
  onDemoClick: () => void;
}

const Solution: React.FC<Props> = ({onDemoClick}) => {
  const [showDemoModal, setShowDemoModal] = useState(false);

  useEffect(() => {
    initScrollAnimations();
  }, []);

  const showKakaoModal = () => {
    window.open(import.meta.env.VITE_KAKAO_CHANNEL_URL, "_blank");
  };

  return (
    <>
      <section id="solution" className="solution">
        <div className="container">
          <div className="solution-wrapper fade-in">
            <div className="solution-content">
              <h2 className="solution-title">지금 바로 핏피티를 이용하세요</h2>
              <p className="solution-description">핏피티로 헬스장 관리를 더 간편하고 효율적으로 시작해보세요.</p>
              <div className="solution-buttons">
                <button onClick={onDemoClick} className="solution-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M17 2H7c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-5 18c-.83 0-1.5-.67-1.5-1.5S11.17 17 12 17s1.5.67 1.5 1.5S12.83 20 12 20zm5-4H7V4h10v12z" />
                  </svg>
                  <span>
                    <small>데모 신청</small>핏피티 이용하기
                  </span>
                </button>
                <button onClick={showKakaoModal} className="solution-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 5.94 2 10.5c0 2.42 1.48 4.56 3.79 5.94L5 22l4.53-2.41C10.33 19.86 11.15 20 12 20c5.52 0 10-3.94 10-8.5S17.52 2 12 2z" />
                  </svg>
                  <span>
                    <small>상담하기</small>
                    카카오톡 상담하기
                  </span>
                </button>
              </div>
              <div className="solution-note">
                <p>* Fitrus 체성분 측정 기기도 상담 가능합니다.</p>
              </div>
            </div>
            <div className="solution-image">
              <img src={fitpt_logo} alt="핏피티 앱 스크린샷" />
              <div className="solution-circle-1"></div>
              <div className="solution-circle-2"></div>
            </div>
          </div>
        </div>
      </section>

      {/* Demo Modal */}
      <Modal isOpen={showDemoModal} onClose={() => setShowDemoModal(false)} overlayClose={true} />
    </>
  );
};

export default Solution;
