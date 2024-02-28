## 기획서 : https://docs.google.com/presentation/d/1z4jr0Co8V3uUAD6aiu-QdRDXGyof_AC2lqNwQbT9xn8/edit#slide=id.g2001d676a39_0_0
## 기능명세서 : https://docs.google.com/spreadsheets/d/1GUEy9sU39I8Ufynq-cEf8gLmy51jYimld8wSN3M9sEM/edit#gid=0
---

### 🔸**앱 설명**

- **여행을 같이 할 사람을 찾을 수 있는 앱.**

        **(기존 동행 앱들과 달리 여행 도중 실시간으로 근처의 있는 여행객과 쉽게 만날 수 있는 기능을 추가함)**

### 🔸**앱 프로세스 (AWS EC2 로드밸런싱 등 좀 더 추가하기)**

![위드 도식화](https://github.com/jwtech05/WithAppAndroid/assets/82852457/acc7541b-d465-4817-af90-831120ea8461)


### 🔸**사용 된 기술**

- **앱 개발** : 안드로이드 스튜디오, JAVA
- **네트워크 통신** : HTTP, Socket
- **클라우드 호스팅** : AWS EC2
- **서버 사이드 개발** : PHP, JAVA
- **데이터베이스 관리**: MySQL

🔹**로그인 및 회원가입**

![로그인 회원가입 캡쳐](https://github.com/jwtech05/WithAppAndroid/assets/82852457/f29ffac2-ec84-4df6-bc76-37374d511c6f)


- **설명** :
    - 로그인 : 앱 내 로그인과 네이버 로그인 두 가지로 로그인 할 수 있다.
    - 회원 가입 :
        - 회원 가입을 할 수 있으며 앱 내 회원 가입과 네이버를 통한 회원 가입이 가능하다.
        - 약관 동의/이메일 인증/전화번호 인증/회원 정보/프로필 사진 순으로 가입이 진행된다.
- **사용 기술** :
    - 로그인 : 네이버 로그인 api
    - 회원가입 : PHPMailer, 네이버 SENS, OpenCV
    

🔹**근처에 있는 유저 탐색** 

![위치 기반 캡쳐](https://github.com/jwtech05/WithAppAndroid/assets/82852457/e2376666-8c74-4f4a-ac44-acb2c2245e86)


- **설명** :
    - 나와 일정 거리에 있는 유저에게 채팅 신청을 할 수 있다.
    - 거리,나이,성별을 설정할 수 있으며, 내 위치가 다른 유저에게 보일지 유무도 선택할 수 있다.
- **사용 기술** :
    - 위치 : ****Location Services, Geocoder
    

🔹**동행 피드**

![피드 캡쳐(3)](https://github.com/jwtech05/WithAppAndroid/assets/82852457/fa07fa19-8d26-4ca0-82df-a6d82e9e39c4)


- **설명** :
    - 동행을 구하는 게시글을 올리거나 볼 수 있다.
    - 게시글을 통해 동행 요청을 하거나 받을 수 있다.
    - 동행 게시글을 업로드시 해당 게시글의 단체채팅방이 생성된다.
- **사용 기술** :
    - 피드 불러오기 : paging
    - 단체 채팅방 생성 : 자바 소켓
    

🔹**실시간 채팅** 

![채팅 기능 캡쳐 (3)](https://github.com/jwtech05/WithAppAndroid/assets/82852457/36403d76-5807-42c4-88df-7760251cae97)

- **설명**:
    - 1대1 채팅방 :
        - 같이 동행하고 싶은 유저의 프로필에서 채팅 신청을 할 수 있다.
        - 글과 그림을 전송할 수 있다.
    - 단체 채팅방 :
        - 피드에 동행 모집글을 생성하면 같이 생성된다.
        - 최대 5명의 유저를 받을 수 있다.
        - 방에 입장하려면 방장이 요청을 수락해야 한다.
        - 방장은 강퇴 또는 방을 없앨 수 있으며, 유저들은 방에서 나갈 수 있다.
- **사용 기술** :
    - 채팅 : 자바 소켓, roomDB
