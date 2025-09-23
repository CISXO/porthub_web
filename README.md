


# PortfolioHub


## 프로젝트 개요
> 이 프로젝트는 각 분야 별로 흩어진 포트폴리오 및 작업물을 하나의 플랫폼으로
관리하고, 협업을 촉진하는 웹 서비스입니다.

- `주요 기능`: 포트폴리오, 채팅, 멘토링
- `인원` : 개발 3인
- `기간`

  - 설계 기간: 2024.01.02 ~ 2024.03.01
  - 개발 기간: 2024.03.02 ~ 2024.06.19
---

- `역할`: 팀장
- `주요 기여`
   - 로그인 및 회원 가입: 암호화, 유효성 처리, 메일 인증, session인증 방식, 이메일 및 유저 아이디 중복 방지
   - 포트폴리오 파트: 검색, 인기 유저 갱신, 조회 수 중복 방지, 페이징, 방문 기록, 좋아요 기능 등
   - CI/CD: 무중단 배포 구축
   - 백오피스: 관리자 기능, 사용자 제재(로그인 차단), 저작권 신고
     - hasRole 4권한(USER, MENTO, ADMIN, BAN)
   - 사용자 페이지: 팔로우 및 팔로잉
   - ERD 설계
     - 포트폴리오, 멘토링
   - 문서화 작업(UML- 유스케이스, DB 테이블 명세서, 화면설계서)
   - 일정관리 (WBS)
   - 모니터링 추가

--- 


## 🛠️ 프로젝트 아키텍처

![아키텍처](https://github.com/user-attachments/assets/f7f468a9-0fce-467e-b295-4be758d3bfd2)

---

## 🛠 기술 스택

### 📌 Backend && Frontend
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white) 

![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

### 📌 Database
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-DC382D?style=for-the-badge&logo=&logoColor=white) ![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazon-aws&logoColor=white)

### 📌 CI/CD & DevOps
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white) ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazon-aws&logoColor=white) ![AWS CodeDeploy](https://img.shields.io/badge/AWS%20CodeDeploy-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)

![Travis CI](https://img.shields.io/badge/TravisCI-3EAAAF?style=for-the-badge&logo=travisci&logoColor=white)  ![Nginx](https://img.shields.io/badge/Nginx-269539?style=for-the-badge&logo=nginx&logoColor=white)

### 📌 Monitoring
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)  ![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)

### 📌 협업 도구
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)  ![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)    ![WBS](https://img.shields.io/badge/WBS-007ACC?style=for-the-badge&logo=&logoColor=white)

---

## 🛠️ Refactoring

### 🔹 대량 데이터 페이징 최적화
> 약 10만개의 테스트 데이터 추가
- **기존**: 페이징 시 전체 데이터 검색으로 인해 **레이턴시 발생**
- **개선**: 페이징에 필요한 20개의 데이터만 불러오도록 코드 수정

### 🔹 검색 성능 분석
- `EXPLAIN ANALYZE`를 활용하여 코드 실행 계획 확인
- **FullText Index** 적용

### 🔹 성능 테스트 및 검증
- **JMeter**를 활용한 부하 테스트 진행
- 최적화 전후 성능 비교 → **검색 성능 16.5% 향상** 

### 🔹 불필한 특정 트랜잭션 제거 및 추가
- **조회**의 경우 트랜잭션 제거
- **업데이트 연관 작업** 시 트랜잭션 추가


## **Indexing을 통한 성능 개선 요약**

### 🔹 **Database**
#### **B-tree 구조 InnoDB**
![table](https://velog.velcdn.com/images/cisxo/post/01fc341c-206a-4f48-858b-1652b8bf16e3/image.png)

### 🔹 **기존 코드 문제점**
- `Portfolios` 테이블에서 `Title`을 검색할 때 `LIKE CONCAT('%', #{searchQuery}, '%')` 사용.
- `EXPLAIN` 결과, **인넷을 효과적으로 활용하지 못하고** `Using where; Using temporary; Using filesort` 발생.


### 🔹 **성능 개선 방법**

#### **1. `FULLTEXT INDEX` 적용**
```sql
CREATE FULLTEXT INDEX idx_title ON Portfolios(Title);
```
✅ **기존 `LIKE` 검색 대시 `MATCH(Title) AGAINST(...)` 활용하여 성능 개선**

#### **2. 성능 테스트**
- **10만 개 데이터 기준**
- **10개 스레드 동시 실행, 10회 복잡 측정**
- **`EXPLAIN` 확인 및 실제 DB 응답 시간 측정**


### 🔹 **성능 비교 결과**
| 테스트 | 평균 실행 시간 (ms) | 개선 |
|----------------|------------------|--------|
| **기존 코드 (`LIKE`)** | 113ms | - |
| **개선 코드 (`FULLTEXT INDEX`)** | 95ms | **16.5%** |

#### **3. `EXPLAIN` 결과 차이점**
- **기존 코드**: `Using index condition; Using where; Using temporary; Using filesort`
- **개선 코드**: `Using where; Using filesort`, `type=fulltext`, `key=idx_title` 활용

---

## ✅ **결론**
- `FULLTEXT INDEX` 적용으로 **검색 속도 약 16.5% 향상**
- **페이징 최적화**로 전체 데이터 검색 문제 해결
- **트랜잭션 제거 및 변경**으로 불필요한 오버헤드 감소 및 데이터 정확성 유지

---


## 📌 서비스 소개
> 통합 포트폴리오 웹 서비스

### 회원
* 포트폴리오를 등록하고 관리가 가능합니다
  <img width="1605" alt="Image" src="https://github.com/user-attachments/assets/b12aed6b-8997-4c74-971b-2034b556f2ad" />

### 멘토 인증 절차
* 멘토 인증 회원만 수익화가 가능합니다.
  <img width="1593" alt="Image" src="https://github.com/user-attachments/assets/340b9ae9-1966-46f4-839a-c6e6bb4b7b55" />

## 🎥 미리 보기


| ![메인홈](https://github.com/user-attachments/assets/b6b5db71-f636-4b59-a2a9-f32f04603ecc) | ![검색](https://github.com/user-attachments/assets/93210e8d-2ae8-46c4-9329-c9d4698f31c7) |
|:----------:|:----------:|
| **메인 홈** | **검색 기능** |
| ![결제](https://github.com/user-attachments/assets/628bc065-e48b-4cfc-a278-90877c90c6fa) | ![멘토 인증](https://github.com/user-attachments/assets/8be2310f-2b1d-4670-9599-6901142aa910) |
| **결제 시스템** | **멘토 인증** |
| ![멘토 등록](https://github.com/user-attachments/assets/834d71a0-a3dd-412e-bc4d-9a94ae1a2ab2) | ![무중단 배포](https://github.com/user-attachments/assets/9b1db388-87b6-4cb5-8c51-7ee7c742d321) |
| **멘토 등록** | **무중단 배포** |
| ![사용자 제재](https://github.com/user-attachments/assets/977801a0-5982-436c-8717-4ed18701b922) | ![어드민](https://github.com/user-attachments/assets/9fe5d1d3-5ad8-49e0-b64a-ca7d79ea68e8) |
| **사용자 제재** | **어드민 페이지** |
| ![채팅](https://github.com/user-attachments/assets/ce4ce4fc-8c3e-41bb-980e-4d3ac082282e) | ![챗봇](https://github.com/user-attachments/assets/2ca102ce-7d34-4eb5-931a-71d21d52dfa0) |
| **채팅 기능** | **챗봇 시스템** |
| ![포트폴리오 등록](https://github.com/user-attachments/assets/9cb2f41d-9baf-4e1a-86a1-27980e6a0c27) | ![포트폴리오 기능](https://github.com/user-attachments/assets/32879e86-7b29-43aa-9017-d691ece1459c) |
| **포트폴리오 관리** | **포트폴리오 기능** |
| ![프로필 수정](https://github.com/user-attachments/assets/27532d7c-583d-4e13-8a29-b7bfbf52ef0e) | ![회원가입](https://github.com/user-attachments/assets/982e14ea-beba-44c1-ba0d-fbb80e019ceb) |
| **프로필 수정** | **회원가입** |

---

## YML && DataBase
 - <details>
   <summary>application.yml 파일을 src/main/resources에 생성</summary>
   <div markdown="1">
   
   ```
     server:
      port: 8080

    spring:
      datasource:
        driver-class-name: org.mariadb.jdbc.Driver
        url: /* */
        username: /* */
        password: /* */
      servlet:
        multipart:
          #file upload size
          max-file-size: 20MB
          max-request-size: 20MB
      mail:
        host: /* */
        port: /* */
        username: /* */
        password: /* */
        properties:
          mail:
            smtp:
              auth: true
              starttls:
                enable: true
    #            required: true
    #          connectiontimeout: 5000
    #          timeout: 5000
    #          writetimeout: 5000
    #    auth-code-expiration-millis: 300000
    
    #Colud S3
    cloud:
      aws:
        s3:
          bucket: /* */
        stack.auto: false
        region.static: ap-northeast-2
        credentials:
          accessKey: /* */
          secretKey: /* */
    
    
    # mybatis
    mybatis:
      mapper-locations: classpath:mapper/*.xml
      config-location: classpath:mybatis-config.xml
    
    #iamport
    imp:
      code: /* */
      api:
        key: /* */
        secretkey: /* */
    
    chatGpt:
      key: /* */
   
    management:
      server:
        port: 9292
      endpoint:
        health:
          show-details: always
      endpoints:
       web:
         base-path: /actuator
         exposure:
           include: "*"
    
   ```
   </div>
   </details>
   
 - <details>
   <summary>Database</summary>
   <div markdown="1">
   
   ```
  
    CREATE TABLE `Categories` (
      `CategoryID` int(11) NOT NULL AUTO_INCREMENT,
      `CategoryName` varchar(255) NOT NULL,
      PRIMARY KEY (`CategoryID`)
    )
  
    CREATE TABLE `CopyrightReport` (
      `ReportID` int(11) NOT NULL AUTO_INCREMENT,
      `PortfolioID` int(11) NOT NULL,
      `Contents` text NOT NULL,
      `ReporterEmail` varchar(255) NOT NULL,
      `ReportedID` int(11) NOT NULL,
      `ReportState` tinyint(1) NOT NULL DEFAULT 0,
      PRIMARY KEY (`ReportID`)
    ) 
    
    CREATE TABLE `Follows` (
      `FollowerID` int(11) NOT NULL,
      `FollowingID` int(11) NOT NULL,
      PRIMARY KEY (`FollowerID`,`FollowingID`)
    ) 
    
    CREATE TABLE `Mento` (
      `MentoID` int(11) NOT NULL AUTO_INCREMENT,
      `UserID` int(11) NOT NULL,
      `Introduction` text DEFAULT NULL,
      `Ability` varchar(255) DEFAULT NULL,
      `CompanyName` varchar(255) DEFAULT NULL,
      `CareerCertification` longtext DEFAULT NULL,
      `UnivName` varchar(255) DEFAULT NULL,
      `UnivCertification` longtext DEFAULT NULL,
      `CertificationName` varchar(255) DEFAULT NULL,
      `IssueCertification` longtext DEFAULT NULL,
      `credit` int(11) DEFAULT 0,
      PRIMARY KEY (`MentoID`)
    ) 
    
    CREATE TABLE `MentoFile` (
      `MentoFileID` int(11) NOT NULL AUTO_INCREMENT,
      `ActivityID` int(11) NOT NULL,
      `File_url` varchar(255) DEFAULT NULL,
      `contents` text DEFAULT NULL,
      PRIMARY KEY (`MentoFileID`)
    ) 
    
    CREATE TABLE `MentoProcess` (
      `ProcessID` int(11) NOT NULL AUTO_INCREMENT,
      `MentoID` int(11) NOT NULL,
      `Process` varchar(1) NOT NULL,
      PRIMARY KEY (`ProcessID`)
    ) 
    
    CREATE TABLE `Mentoring` (
      `MentoringID` int(11) NOT NULL AUTO_INCREMENT,
      `MentoID` int(11) NOT NULL,
      `CategoryID` int(11) DEFAULT NULL,
      `Title` varchar(255) DEFAULT NULL,
      `Content` text DEFAULT NULL,
      `Price` decimal(10,2) DEFAULT NULL,
      `Thumbnail` varchar(255) DEFAULT NULL,
      `file_urls` longtext DEFAULT NULL,
      `mentoring_delete` int(11) DEFAULT 0,
      PRIMARY KEY (`MentoringID`)
    ) 
    
    CREATE TABLE `MentoringOrder` (
      `OrderID` int(11) NOT NULL AUTO_INCREMENT,
      `pg` varchar(50) NOT NULL,
      `pay_method` varchar(50) NOT NULL,
      `merchant_uid` text DEFAULT NULL,
      `goods_id` int(11) NOT NULL,
      `pay_name` longtext NOT NULL,
      `amount` int(11) NOT NULL,
      `buyer_email` varchar(50) NOT NULL,
      `buyer_name` varchar(50) NOT NULL,
      PRIMARY KEY (`OrderID`)
    ) 
    
    CREATE TABLE `PopularUser` (
      `PopularID` int(11) NOT NULL AUTO_INCREMENT,
      `UserID` int(11) NOT NULL,
      `UserName` varchar(255) NOT NULL,
      `Email` varchar(255) NOT NULL,
      `ProfileImage` longtext DEFAULT NULL,
      `aff` longtext DEFAULT NULL,
      PRIMARY KEY (`PopularID`)
    ) 
    
    CREATE TABLE `PortfolioLikes` (
      `PortfolioLikesID` int(11) NOT NULL AUTO_INCREMENT,
      `PortfolioID` int(11) NOT NULL,
      `Email` varchar(255) NOT NULL,
      `Heart_Check` tinyint(1) NOT NULL DEFAULT 0,
      PRIMARY KEY (`PortfolioLikesID`)
    ) 
    
    CREATE TABLE `Usermeta` (
      `MetaID` int(11) NOT NULL AUTO_INCREMENT,
      `UserID` int(11) NOT NULL,
      `intro` longtext DEFAULT NULL,
      `aff` longtext DEFAULT NULL,
      `link` longtext DEFAULT NULL,
      `career` longtext DEFAULT NULL,
      PRIMARY KEY (`MetaID`)
    ) 
    
    CREATE TABLE `Users` (
      `UserID` int(11) NOT NULL AUTO_INCREMENT,
      `UserName` varchar(255) NOT NULL,
      `Email` varchar(255) NOT NULL,
      `PasswordHash` varbinary(255) NOT NULL,
      `ProfileImage` longtext DEFAULT NULL,
      `RegistrationDate` datetime NOT NULL,
      `AdditionalInfo` text DEFAULT NULL,
      `Role` text DEFAULT 0,
      `backImage` longtext DEFAULT NULL,
      `PaidProduct` longtext DEFAULT NULL,
      PRIMARY KEY (`UserID`),
      UNIQUE KEY `Email` (`Email`)
    ) 
    
    CREATE TABLE `Chats` (
      `ChatID` int(11) NOT NULL AUTO_INCREMENT,
      `SenderID` int(11) NOT NULL,
      `ReceiverID` int(11) NOT NULL,
      `Content` text NOT NULL,
      `DateTime` datetime NOT NULL,
      `SessionID` varchar(64) DEFAULT NULL,
      PRIMARY KEY (`ChatID`),
      KEY `SenderID` (`SenderID`),
      KEY `ReceiverID` (`ReceiverID`),
      KEY `fk_ChatSessions` (`SessionID`),
      CONSTRAINT `Chats_ibfk_1` FOREIGN KEY (`SenderID`) REFERENCES `Users` (`UserID`),
      CONSTRAINT `Chats_ibfk_2` FOREIGN KEY (`ReceiverID`) REFERENCES `Users` (`UserID`)
    ) 
    
    CREATE TABLE `Payments` (
      `PaymentID` int(11) NOT NULL AUTO_INCREMENT,
      `PayerID` int(11) NOT NULL,
      `PayeeID` int(11) NOT NULL,
      `PaymentDate` datetime NOT NULL,
      `Amount` decimal(10,2) NOT NULL,
      `PaymentStatus` varchar(255) NOT NULL,
      PRIMARY KEY (`PaymentID`),
      KEY `PayerID` (`PayerID`),
      KEY `PayeeID` (`PayeeID`),
      CONSTRAINT `Payments_ibfk_1` FOREIGN KEY (`PayerID`) REFERENCES `Users` (`UserID`),
      CONSTRAINT `Payments_ibfk_2` FOREIGN KEY (`PayeeID`) REFERENCES `Users` (`UserID`)
    ) 
    
    CREATE TABLE `Portfolios` (
      `PortfolioID` int(11) NOT NULL AUTO_INCREMENT,
      `AuthorID` int(11) NOT NULL,
      `Thumbnail_url` varchar(255) DEFAULT NULL,
      `Title` varchar(255) NOT NULL,
      `Hearts_count` int(11) DEFAULT NULL,
      `Views_count` int(11) DEFAULT NULL,
      `CreationDate` datetime NOT NULL,
      `CategoryID` int(11) DEFAULT NULL,
      `AttachmentsOrLinks` text DEFAULT NULL,
      PRIMARY KEY (`PortfolioID`),
      KEY `AuthorID` (`AuthorID`),
      KEY `CategoryID` (`CategoryID`),
      CONSTRAINT `Portfolios_ibfk_1` FOREIGN KEY (`AuthorID`) REFERENCES `Users` (`UserID`),
      CONSTRAINT `Portfolios_ibfk_2` FOREIGN KEY (`CategoryID`) REFERENCES `Categories` (`CategoryID`)
    ) 
    
    CREATE TABLE `SessionParticipants` (
      `SessionKey` varchar(64) NOT NULL,
      `UserID` int(11) NOT NULL,
      PRIMARY KEY (`SessionKey`,`UserID`),
      KEY `UserID` (`UserID`),
      KEY `idx_SessionParticipants_SessionKey` (`SessionKey`),
      CONSTRAINT `SessionParticipants_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`UserID`)
    ) 
    
    CREATE TABLE `Images` (
      `ImageID` int(11) NOT NULL AUTO_INCREMENT,
      `PortfolioID` int(11) DEFAULT NULL,
      `Image_url` varchar(255) DEFAULT NULL,
      `contents` mediumtext DEFAULT NULL,
      PRIMARY KEY (`ImageID`),
      KEY `PortfolioID` (`PortfolioID`),
      CONSTRAINT `Images_ibfk_1` FOREIGN KEY (`PortfolioID`) REFERENCES `Portfolios` (`PortfolioID`)
    ) 
    
    CREATE TABLE `PortfolioTags` (
      `TagID` int(11) NOT NULL AUTO_INCREMENT,
      `PortfolioID` int(11) NOT NULL,
      `TagName` varchar(255) NOT NULL,
      PRIMARY KEY (`TagID`),
      KEY `PortfolioID` (`PortfolioID`),
      CONSTRAINT `PortfolioTags_ibfk_1` FOREIGN KEY (`PortfolioID`) REFERENCES `Portfolios` (`PortfolioID`)
    ) 
   ```
   </div>
   </details>
   
