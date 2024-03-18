## 🙌 Hello. Pyre is always with you!
<img src="https://cdn.discordapp.com/attachments/1214849763745202176/1214850895133679616/pyre.png?ex=65fa9d10&is=65e82810&hm=0824d809c6b9297212831b1bcac723e24bf93b2199ffbcb665e84092034a133d&" alt="drawing" width="400"/>

#### 현재 파이어는 미완성 프로젝트입니다.
#### [Github 조직](https://github.com/Pyre-org)

# PyreAuth
## 프로젝트 구조
<img src="https://cdn.discordapp.com/attachments/393025698907947009/1219257755987083375/image.png?ex=660aa545&is=65f83045&hm=d83ce918befeee89b1ae8ce1c229a822dc11bac1f86d1acad5d30deafc1249dc&" alt="drawing" width="600"/>

## 기술 스택
- Java 21
- Spring Boot (3.2.2)
- Spring Web
- Spring Data JPA
- Spring Cloud eureka client
- Spring Cloud config
- Spring cloud Open Feign

- Redis
- Mysql

  
## 커뮤니티 서비스
- 관계형 데이터베이스를 사용하여 파이어 커뮤니티의 채널-룸-스페이스 계층의 데이터를 관리합니다.
- 엔티티
  - Channel : 채널을 구성하는 엔티티입니다.
    - Channel (One-Many) Room
    - Channel (One-Many) ChannelEndUser
  - ChannelEndUser : 채널에 가입한 유저를 구성하기 위한 엔티티입니다.
    - ChannelEndUser (Many-One) Channel
    - ChannelEndUser (One-One) userId (UUID) - Auth 서비스와 Feign 통신을 통해 참조합니다.
    - ChannelEndUser (One-Many) RoomEndUser
  - Room : 룸을 구성하기 위한 엔티티입니다.
   - Room (Many-One) Channel
   - Room (One-Many) RoomEndUser
   - Room (One-Many) Space
  - RoomEndUser : 룸에 가입한 유저를 구성하기 위한 엔티티입니다. (유저에 대한 정규화 필요)
    - RoomEndUser (Many-One) Channel
    - RoomEndUser (Many-One) ChannelEndUser
    - RoomEndUser (Many-One) userId (UUID) - Auth 서비스와 Feign 통신을 통해 참조합니다.
  - Space : 스페이스를 구성하기 위한 엔티티입니다.
    - Space (Many-One) Room
- Redis를 사용하여 기간제 룸 초대권을 생성 및 관리합니다.
- Open API [링크](https://apis.pyre.live/community/swagger-ui/index.html)

## 구현 중 이슈
각 유저 별로 룸 순서를 다르게 배치하고, 설정할 수 있도록 하기 위해 자기 참조 방식을 사용하여 연결 리스트로 방 순서를 구현했습니다.
RoomEndUser (One-One) prevRoomEndUser
RoomEndUser (One-One) nextRoomEndUser
룸 순서를 변경하기 전에는 다음 노드를 참조할 수 있었지만, 룸 순서를 변경하는 로직에서 Update 할 수 없는 문제가 생겼습니다. 
OneToOne 매핑임을 인지하고, 하나의 노드가 여러 개로부터 참조되지 않도록 로직을 구성했으나, 똑같은 문제가 발생했습니다.

### 해결
OneToOne 매핑을 사용하지 않고, UUID를 저장하는 방식으로 다음 노드, 이전 노드를 기록하여 연결 리스트를 구현 했습니다.
