import { useEffect } from "react";
import { initScrollAnimations } from "../utils/animations";
import "../styles/Features.css";
import connection from "../assets/connection.svg";
import calendar_reminder from "../assets/calendar_reminder.svg";

const Features: React.FC = () => {
  useEffect(() => {
    initScrollAnimations();
  }, []);

  return (
    <section id="features" className="features section">
      <div className="container">
        <div className="section-header-feature fade-in">
          <h2 className="section-title-feature">주요 기능</h2>
          <p className="section-subtitle-feature">핏피티를 통해 헬스장 관리부터 건강데이터 측정, 운동 관리까지 모든 과정을 스마트하게 경험해보세요.</p>
        </div>

        <div className="features-grid">
          <div className="feature-card fade-in">
            <div className="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                <line x1="8" y1="21" x2="16" y2="21"></line>
                <line x1="12" y1="17" x2="12" y2="21"></line>
              </svg>
            </div>
            <h3 className="feature-title">통합 솔루션 제공</h3>
            <p className="feature-description">센터 운영자는 웹으로, 트레이너는 태블릿 전용 앱으로, 회원은 모바일 앱으로 체계적인 PT 관리와 건강 데이터를 주고받을 수 있는 통합 관리 환경을 제공합니다.</p>
          </div>

          <div className="feature-card fade-in">
            <div className="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
            </div>
            <h3 className="feature-title">PT 기록 시스템</h3>
            <p className="feature-description">운동 수행 이력, 체성분 변화, 상담 내용을 자동 저장하며, 리포트 형식으로 내보내어 회원과 효과적으로 공유할 수 있습니다.</p>
          </div>

          <div className="feature-card fade-in">
            <div className="feature-icon">
              <img src={connection} alt="노트북 연동 자가진단" />
            </div>
            <h3 className="feature-title">모바일 태블릿 연동 관리</h3>
            <p className="feature-description">Fitrus 기기와 앱을 연동하여 회원의 체성분 상태를 실시간으로 수집합니다.</p>
          </div>

          <div className="feature-card fade-in">
            <div className="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                <circle cx="8.5" cy="8.5" r="1.5"></circle>
                <polyline points="21 15 16 10 5 21"></polyline>
              </svg>
            </div>
            <h3 className="feature-title">운동 부위 가이드</h3>
            <p className="feature-description">트레이너가 정확한 PT 분석을 제공할 수 있게 정면, 후면 등 다양한 각도의 근육들에 대한 안내 가이드를 제공합니다.</p>
          </div>

          <div className="feature-card fade-in">
            <div className="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="3" width="7" height="7"></rect>
                <rect x="14" y="3" width="7" height="7"></rect>
                <rect x="14" y="14" width="7" height="7"></rect>
                <rect x="3" y="14" width="7" height="7"></rect>
              </svg>
            </div>
            <h3 className="feature-title">관리자 웹 대시보드</h3>
            <p className="feature-description">헬스장 관리와 트레이너에 대한 기록들을 관리자 웹에서 편리하게 확인하고 관리할 수 있습니다.</p>
          </div>

          <div className="feature-card fade-in">
            <div className="feature-icon">
              <img src={calendar_reminder} alt="반납 일정 관리" />
            </div>
            <h3 className="feature-title">PT 일정 관리</h3>
            <p className="feature-description">회원별 수업 예약, 트레이너 일정, 알림 등 PT 일정을 스마트하게 관리하고 캘린더 기반으로 한눈에 확인할 수 있습니다.</p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Features;
