# FilterSnap - Java 이미지 필터 프로젝트

Java Swing 기반의 이미지 필터 도구입니다.  
이미지를 열고 다양한 필터(R/G/B/흑백)를 적용하거나 밝기를 조절하고, 픽셀 클릭 시 RGB 값을 추출할 수 있습니다.

## 주요 기능
- 이미지 열기 및 저장
- 빨강 / 초록 / 파랑 / 흑백 필터
- 밝기 조절 슬라이더
- 마우스 클릭으로 RGB 및 HEX 색상 추출
- 원본 vs 결과 비교 (JSplitPane + JScrollPane)

## 사용 기술
- Java 17
- Swing GUI (JFrame, JPanel, JLabel, JSlider, JButton)
- BufferedImage, Graphics2D, ImageIO, MouseEvent 등

## 실행 화면 예시
### 💡 원본 / 필터 비교
![filter_comparison](https://github.com/user-attachments/assets/b9eb578d-fa3e-4c29-9cff-537e847a8d59)

### 🔆 밝기 조절 슬라이더
![brightness_demo1](https://github.com/user-attachments/assets/7354bca7-e712-46d0-9020-04e5c6ff627e)

### 🎯 마우스 클릭 색상 추출
![color_pick_popup](https://github.com/user-attachments/assets/4e011190-e0e4-4ec0-a3f2-4179df8204d1)
![color_pick_popup2](https://github.com/user-attachments/assets/4ae464f3-dab6-4dbc-a179-7d419cc7b18c)

## 프로젝트 설명 페이지
👉 [Notion 포트폴리오 바로가기](https://www.notion.so/FilterSnap-Java-1f6e7a8b645c80c9aebdf0a16af199b4?showMoveTo=true&saveParent=true)
