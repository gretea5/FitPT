import { useEffect, useRef } from "react";
import { typeWriter } from "../utils/animations";
import "../styles/Fitory.css";
import icon1 from "../assets/icon1.png";
import icon2 from "../assets/icon2.png";
import icon3 from "../assets/icon3.png";
import icon4 from "../assets/icon4.png";
import icon5 from "../assets/icon5.png";
import icon6 from "../assets/icon6.png";
import icon7 from "../assets/icon7.png";
import icon8 from "../assets/icon8.png";
import icon9 from "../assets/icon9.png";

interface Props {
  onDemoClick: () => void;
}

const Fitory: React.FC<Props> = ({onDemoClick}) => {
  const titleRef = useRef<HTMLHeadingElement>(null);

  useEffect(() => {
    // 타이틀 타이핑 애니메이션 시작
    if (titleRef.current) {
      // 화면 크기를 확인하여 반응형인지 체크
      const isResponsive = window.innerWidth <= 992;

      // 플레이스홀더와 타이핑 요소 설정
      const placeholderHTML = isResponsive
        ? '<div class="title-placeholder" style="text-align:center">핏피티와 함께하는<br>스마트한 피트니스 솔루션</div>'
        : '<div class="title-placeholder">핏피티와 함께하는<br>스마트한 피트니스 솔루션</div>';

      titleRef.current.innerHTML = placeholderHTML + '<span class="typing-text"></span>';

      // 타이핑 애니메이션 적용
      const typingElement = titleRef.current.querySelector(".typing-text");
      if (typingElement) {
        typeWriter(typingElement as HTMLElement, "핏피티와 함께하는<br>스마트한 피트니스 솔루션", 100);
      }
    }
  }, []);

  const showKakaoModal = () => {
    window.open(import.meta.env.VITE_KAKAO_CHANNEL_URL, "_blank");
  };


  return (
    <section className="fitory">
      <div className="fitory-background-shapes">
        <div className="shape shape-1"></div>
        <div className="shape shape-2"></div>
        <div className="shape shape-3"></div>
      </div>

      <div className="container">
        <div className="fitory-grid">
          <div className="fitory-content">
            <h1 ref={titleRef} className="fitory-title"></h1>
            <div className="fitory-tagline">
              <span className="highlight">Fitrus</span>와 함께 <span className="highlight">헬스장 관리</span>부터, <span className="highlight">건강 관리</span>까지
            </div>

            <div className="fitory-features">
              <div className="feature-item">
                <div className="check-icon">✓</div>
                <div className="feature-text">헬스장 관리 솔루션</div>
              </div>
              <div className="feature-item">
                <div className="check-icon">✓</div>
                <div className="feature-text">스마트 체성분 측정</div>
              </div>
              <div className="feature-item">
                <div className="check-icon">✓</div>
                <div className="feature-text">운동 목표, 건강 관리 설정</div>
              </div>
            </div>

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

            <div className="fitory-caption">
              <a href="#how-it-works" className="link-how">
                핏피티가 처음이신가요? 이용 방법 알아보기 →
              </a>
            </div>
          </div>

          <div className="fitory-image">
            <div className="fitpt-character">
              <div className="floating-icons">
                <img src={icon1} alt="아이콘" className="floating-icon icon-1" />
                <img src={icon3} alt="아이콘" className="floating-icon icon-3" />
                <img src={icon4} alt="아이콘" className="floating-icon icon-4" />
                <img src={icon5} alt="아이콘" className="floating-icon icon-5" />
                <img src={icon7} alt="아이콘" className="floating-icon icon-7" />
                <img src={icon8} alt="아이콘" className="floating-icon icon-8" />
              </div>

              <img src={icon2} alt="핏피티 캐릭터" className="fitpt-main" />
              <img src={icon6} alt="아이콘" className="floating-icon icon-6" />
              <img src={icon9} alt="아이콘" className="floating-icon icon-9" />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Fitory;
