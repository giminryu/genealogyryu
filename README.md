# 종친회 홈페이지

Spring Boot 기반의 종친회 관리 시스템입니다. 회원 관리, 공지사항 관리, 대시보드 등의 기능을 제공합니다.

## 🚀 주요 기능

### 회원 관리
- 회원 등록/수정/삭제
- 회원 검색 (이름, 전화번호, 이메일, 주소, 항렬별)
- 회원 목록 조회
- 항렬별 회원 통계

### 공지사항 관리
- 공지사항 작성/수정/삭제
- 중요 공지사항 표시
- 조회수 추적
- 공지사항 검색 (제목, 작성자별)

### 대시보드
- 전체 회원 수 통계
- 항렬별 회원 분포
- 최근 가입한 회원 목록
- 최신 공지사항

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.4, Java 17
- **Database**: H2 Database (In-Memory)
- **Frontend**: Thymeleaf, Bootstrap 5.3.0
- **Build Tool**: Maven

## 📋 요구사항

- Java 17 이상
- Maven 3.6 이상

## 🚀 실행 방법

### 1. 프로젝트 클론
```bash
git clone [repository-url]
cd genealogryu
```

### 2. 애플리케이션 실행
```bash
mvn spring-boot:run
```

### 3. 웹 브라우저에서 접속
```
http://localhost:8080
```

## 📁 프로젝트 구조

```
src/main/java/com/genealogy/genealogryu/
├── GenealogryuApplication.java          # 메인 애플리케이션 클래스
├── config/
│   └── DataInitializer.java             # 샘플 데이터 초기화
├── controller/
│   ├── HomeController.java              # 홈페이지 컨트롤러
│   ├── MemberController.java            # 회원 관리 컨트롤러
│   └── NoticeController.java            # 공지사항 컨트롤러
├── entity/
│   ├── Member.java                      # 회원 엔티티
│   └── Notice.java                      # 공지사항 엔티티
├── repository/
│   ├── MemberRepository.java            # 회원 리포지토리
│   └── NoticeRepository.java            # 공지사항 리포지토리
└── service/
    ├── MemberService.java               # 회원 서비스
    └── NoticeService.java               # 공지사항 서비스
```

## 🗄 데이터베이스

### H2 Console 접속
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:genealogy`
- Username: `sa`
- Password: (비어있음)

### 주요 테이블
- `members`: 회원 정보
- `notices`: 공지사항

## 📱 주요 페이지

### 홈페이지 (`/`)
- 종친회 소개
- 최신 공지사항
- 통계 정보
- 빠른 링크

### 회원 관리 (`/members`)
- 회원 목록 조회
- 회원 등록 폼
- 회원 검색
- 회원 정보 수정/삭제

### 공지사항 (`/notices`)
- 공지사항 목록
- 공지사항 작성/수정
- 중요 공지사항 필터링
- 인기 공지사항 조회

### 대시보드 (`/dashboard`)
- 회원 통계
- 항렬별 분포
- 최근 활동

## 🎨 UI/UX 특징

- **반응형 디자인**: 모바일, 태블릿, 데스크톱 지원
- **모던한 UI**: Bootstrap 5 기반의 깔끔한 디자인
- **직관적인 네비게이션**: 사용자 친화적인 메뉴 구조
- **실시간 피드백**: 작업 결과에 대한 즉각적인 알림

## 🔧 설정

### application.properties 주요 설정
```properties
# H2 Database 설정
spring.datasource.url=jdbc:h2:mem:genealogy
spring.h2.console.enabled=true

# JPA 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# 서버 포트
server.port=8080
```

## 📊 샘플 데이터

애플리케이션 시작 시 자동으로 다음 샘플 데이터가 생성됩니다:

### 샘플 회원 (5명)
- 김철수 (25세, 서울시 강남구)
- 이영희 (26세, 서울시 서초구)
- 박민수 (24세, 경기도 성남시)
- 최지영 (27세, 인천시 남동구)
- 정현우 (26세, 부산시 해운대구)

### 샘플 공지사항 (5개)
- 2024년 종친회 정기총회 안내 (중요)
- 족보 발간 사업 진행상황
- 청년회 활동 안내
- 종친회관 이용 안내
- 2024년 설날 대제례 안내 (중요)

## 🚀 배포

### JAR 파일 생성
```bash
mvn clean package
```

### JAR 파일 실행
```bash
java -jar target/genealogryu-0.0.1-SNAPSHOT.jar
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

- 이메일: info@genealogy.com
- 전화: 02-1234-5678
- 주소: 서울시 강남구 종친회관

---

**종친회 홈페이지** - 우리 가문의 전통을 이어가는 디지털 공간
