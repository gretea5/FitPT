import { useEffect } from "react";
import "../styles/HowItWorks.css";
import mobileFrame from "../assets/mobile-frame.png";
import step1_gif from "../assets/step1.gif";
import step2_gif from "../assets/step2.gif";
import step3_gif from "../assets/step3.gif";

const HowItWorks = () => { 
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible");
          }
        });
      },
      {
        threshold: 0.2,
      }
    );

    document.querySelectorAll(".step-item").forEach((item) => {
      observer.observe(item);
    });

    return () => observer.disconnect();
  }, []);

  return (
    <section id="how-it-works" className="how-it-works">
      <div className="container">
        <div className="section-header">
          <h2 className="section-title">핏피티 솔루션 도입 절차</h2>
          <p className="section-subtitle">핏피티 솔루션과 Fitrus를 이용한 헬스장 관리부터 건강 관리까지 모두 알려드립니다.</p>
        </div>

        <div className="steps-container">
          <div className="step-item">
            <div className="step-content-wrapper">
              <div className="step-header">
                <div className="step-number">1</div>
                <h3 className="step-title">핏피티 솔루션 상담</h3>
              </div>
              <div className="step-content">
                <p className="step-description">
                  스마트 피트니스 환경을 구축하고 싶으신가요?<br />
                  핏피티 솔루션과 Fitrus 기기 연동 방식을 도입하여 헬스장 운영을 디지털화하는 방법을 안내드립니다.
                </p>
              </div>
            </div>
            <div className="step-image-wrapper">
              <div className="mobile-device-container">
                {/* 모바일 프레임 (고정 백그라운드) */}
                <div className="mobile-frame">
                  <img src={mobileFrame} alt="모바일 디바이스 프레임" className="device-frame" />
                </div>
                {/* GIF를 화면 내부에 오버레이 */}
                <div className="screen-content">
                  <img src={step1_gif} alt="노트북 사진 촬영 화면" className="screen-gif" />
                </div>
              </div>
            </div>
          </div>

          <div className="step-item">
            <div className="step-content-wrapper">
              <div className="step-header">
                <div className="step-number">2</div>
                <h3 className="step-title">Fitrus 기기 연동 및 체성분 측정</h3>
              </div>
              <div className="step-content">
                <p className="step-description">
                  회원 및 트레이너는 모바일 앱을 통해 Fitrus 기기와 간편하게 연동하여 체성분, 근육량, 체지방률 등 주요 데이터를 측정하고 확인할 수 있습니다.<br />
                  모든 데이터는 자동으로 사용자 앱 및 트레이너 앱과 연동됩니다.
                </p>
              </div>
            </div>
            <div className="step-image-wrapper">
              <div className="mobile-device-container">
                {/* 모바일 프레임 (고정 백그라운드) */}
                <div className="mobile-frame">
                  <img src={mobileFrame} alt="모바일 디바이스 프레임" className="device-frame" />
                </div>
                {/* GIF를 화면 내부에 오버레이 */}
                <div className="screen-content">
                  <img src={step2_gif} alt="자가진단 프로그램 실행 화면" className="screen-gif" />
                </div>
              </div>
            </div>
          </div>

          <div className="step-item">
            <div className="step-content-wrapper">
              <div className="step-header">
                <div className="step-number">3</div>
                <h3 className="step-title">데이터 기반 맞춤형 관리</h3>
              </div>
              <div className="step-content">
                <p className="step-description">
                  측정된 시각화 데이터를 바탕으로 PT 기록을 저장하고, 맞춤형 건강 목표를 설정합니다.<br />
                  트레이너는 회원의 이력과 변화를 종합적으로 분석하여 개인화된 근육 부위 등 상세한 운동 전략을 제시할 수 있습니다.
                </p>
              </div>
            </div>
            <div className="step-image-wrapper">
              <div className="mobile-device-container">
                {/* 모바일 프레임 (고정 백그라운드) */}
                <div className="mobile-frame">
                  <img src={mobileFrame} alt="모바일 디바이스 프레임" className="device-frame" />
                </div>
                {/* GIF를 화면 내부에 오버레이 */}
                <div className="screen-content">
                  <img src={step3_gif} alt="AI 분석 및 보고서 생성 화면" className="screen-gif" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="action-container">
          <a href="#solution" className="btn-action">
            지금 시작하기
            <svg width="20" height="20" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3.33334 8H12.6667" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M8 3.33334L12.6667 8.00001L8 12.6667" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </a>
        </div>
      </div>
    </section>
  );
};

export default HowItWorks;
