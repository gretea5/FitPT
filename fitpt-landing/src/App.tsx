import { useEffect, useState } from "react";
import Header from "./components/Header";
import Fitory from "./components/Fitory";
import Features from "./components/Features";
import HowItWorks from "./components/HowItWorks";
import Solution from "./components/Solution";
import Footer from "./components/Footer";
import Modal from "./components/Modal";
import "./styles/global.css";
import { initScrollAnimations } from "./utils/animations";

function App() {
  const [showDemoModal, setShowDemoModal] = useState(false);

  useEffect(() => {
    // 스크롤 애니메이션 초기화
    initScrollAnimations();

    // 모바일 메뉴 토글 기능
    const menuToggle = document.querySelector(".menu-toggle");
    const nav = document.querySelector(".nav");

    if (menuToggle && nav) {
      menuToggle.addEventListener("click", () => {
        menuToggle.classList.toggle("active");
        nav.classList.toggle("open");
      });
    }

    // 페이지 로드시 핏토리 캐릭터 섹션의 이미지 애니메이션
    setTimeout(() => {
      const fadeInImages = document.querySelectorAll(".fade-in-image");
      fadeInImages.forEach((img) => {
        img.classList.add("visible");
      });
    }, 500);
  }, []);

  return (
    <>
      <Header />
      <main>
        <Fitory onDemoClick={() => setShowDemoModal(true)} />
        <Features />
        <HowItWorks />
        <Solution onDemoClick={() => setShowDemoModal(true)} />
      </main>
      <Footer onDemoClick={() => setShowDemoModal(true)} />

      <Modal isOpen={showDemoModal} onClose={() => setShowDemoModal(false)} overlayClose={true} />
    </>
    
  );
}

export default App;
