import { useEffect } from "react";
import "../styles/Header.css";
import { initHeaderScroll } from "../utils/animations";

const Header: React.FC = () => {
  useEffect(() => {
    initHeaderScroll();
  }, []);

  return (
    <header className="header">
      <div className="container header-container">
        <a href="/" className="logo">
          <span className="logo-text">핏피티</span>
        </a>

        <nav className="nav">
          <ul className="nav-list">
            <li className="nav-item">
              <a href="#features" className="nav-link">
                주요 기능
              </a>
            </li>
            <li className="nav-item">
              <a href="#how-it-works" className="nav-link">
                이용 방법
              </a>
            </li>
          </ul>
        </nav>

        <div className="header-buttons">
          <a href="#solution" className="btn-solution">
            상담하기
          </a>
        </div>
      </div>
    </header>
  );
};

export default Header;
