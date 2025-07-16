# 🛒 HaggleMarket

중고 거래 + 실시간 경매 + 위치 기반 서비스를 제공하는 플랫폼입니다.  
번개장터 스타일의 UI와 실시간 경매 기능을 통해 사용자들이 직관적이고 편리하게 물품을 거래할 수 있습니다.

## 🚀 주요 기능
- 회원가입/로그인 (JWT 기반 인증)
- 사용자 프로필 및 내 정보 수정
- 상품 등록, 조회, 수정, 삭제
- 찜하기 기능
- 실시간 경매 및 입찰
- 위치 기반 마켓 탐색
- 프로필 사진 업로드 및 관리


## ⚙️ 개발 환경

### Backend
- Java 17
- Spring Boot 3.5
- Spring Data JPA
- MySQL 8
- Lombok
- JWT 기반 인증

### Frontend
- React 18
- Vite
- Axios
- Styled-components / CSS Modules

### 공통
- Git, GitHub
- IntelliJ IDEA (Backend)
- VSCode (Frontend)

---

## 🖥️ 실행 방법

### 1️⃣ 백엔드 실행
1. MySQL DB 생성
   ```sql
   CREATE DATABASE hagglemarket;

## application.properties 설정
spring.datasource.url=jdbc:mysql://localhost:3306/hagglemarket
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

## 프로젝트 빌드 및 실행
./mvnw spring-boot:run

## 프론트엔드 실행
npm install
npm run dev


---

### ✅ **3. 팀 & 기여자**
```markdown
## 👨‍💻 팀원
- 박동수 (Backend, Frontend, API 설계, DB 모델링, UI/UX 설계, 상태관리)
- 이광표 (Backend, API 설계, DB 모델링, Sub Frontend)
- 박준우 (UI/UX 설계)

## 📝 기여 방법
1. Fork 후 새 브랜치 생성
2. 기능 추가 및 수정
3. Pull Request 요청
