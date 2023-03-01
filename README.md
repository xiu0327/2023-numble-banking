# 1. 프로젝트 개요

## 1.1 프로젝트 목표

1. 객체지향 원리를 이해하고 클린 코드 규칙을 준수하여 올바른 코드를 작성합니다.
2. 계좌이체 시 발생 가능한 동시성 문제를 해결하고 트랜잭션 원자성을 보장합니다.
3. 단순한 기능 구현 뿐만 아니라 대용량 트래픽까지 고려한 뱅킹 서버를 구축합니다.
4. 친구 목록 조회 API의 요구사항을 추가하여 기능을 추가해보며 코드 유지보수성이 좋은지 테스트해봅니다.

## 1.2 요구사항 분석 및 도메인 모델링

<img width="800" alt="스크린샷 2023-02-23 오후 10 42 09" src="https://user-images.githubusercontent.com/78461009/221401897-815bd51a-d30e-4c86-a30d-66507aa0b25b.png">

- [요구사항 분석 및 도메인 모델링](https://github.com/xiu0327/2023-numble-banking/wiki/%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EB%B6%84%EC%84%9D-%EB%B0%8F-%EB%8F%84%EB%A9%94%EC%9D%B8-%EB%AA%A8%EB%8D%B8%EB%A7%81)

## 1.3 기술 스택

| SpringBoot | MySQL |  JPA   |  JUnit   |
| :--------: | :--------: | :------: | :-----: |
|   <img width="100" height="100" src="https://user-images.githubusercontent.com/78461009/221402061-1d45c959-b47f-4b9f-b078-d483132b0066.jpg" />   |   <img width="100" height="100" src="https://user-images.githubusercontent.com/78461009/221402167-e521a45a-e55c-4992-a993-747d44c75c21.jpeg" />    | <img width="100" height="100" src="https://user-images.githubusercontent.com/78461009/221402410-676c7da0-fa34-431c-a44a-482edcd6ce41.png" /> | <img width="100" height="100" src="https://user-images.githubusercontent.com/78461009/221402423-7cba955f-cc75-41bd-bb34-120c491bf190.jpg" /> |

# 2. RDB

## 2.1 ERD

<img width="800" alt="스크린샷 2023-02-28 오후 5 11 59" src="https://user-images.githubusercontent.com/78461009/221792815-be1469d7-ca8f-4e1d-81bd-9b177e7febad.png">

## 2.2 RDB vs NoSQL

→ **도메인 특성 상 스키마가 변경되면 안되기 때문에 RDB를 사용한다.**

RDB와 NoSQL의 가장 큰 차이점은 유연성이라 생각한다. RDB는 스키마가 고정되어있지만, NoSQL은 스키마가 유동적이다. 하지만 현재 프로젝트의 DB는 스키마가 유연해선 안된다. 특히 계좌 스키마가 유연하다면 돈을 넣었는데 돈이 없다고 하는 큰 장애가 일어날 수 있다. 따라서 프로젝트의 DB는 NoSQL이 아닌 RDB를 사용하며, RDB 중에서도 MySQL을 사용한다.

## 2.3 MySQL

→ **MySQL의 innoDB는 트랜잭션의 원자성과 동시성 문제를 해결해줄 장점을 지닌다.**

MySQL은 innoDB라는 스토리지 엔진을 사용한다. innoDB 엔진은 여러 장점을 지니고 있는데, 특히 innoDB 엔진은 과제에서 요구하는 트랜잭션 원자성 보장과 동시성 문제를 잘 해결해준다. innoDB 엔진은 레코드 기반의 잠금을 제공하며, 그 때문에 높은 동시성 처리가 가능하고 안정적이며 성능이 뛰어나다. 또한 innoDB 엔진은 MVCC 기술을 이용하여 잠금을 걸지 않고 읽기 작업을 수행한다. 간단하게 말해 다른 트랜잭션이 가진 잠금을 기자리지 않고 읽기 작업이 가능하다.

예를 들어, Member 테이블 하나의 컬럼을 UPDATE 하는 트랜잭션이 들어왔다. 이때 COMMIT 되지 않은 상태에서 SELECT 쿼리문이 들어오면, 별다른 대기 없이 바로 실행된다. innoDB 트랜잭션 격리 수준은 READ COMMIT이 디폴트 값이기 때문에 변경되기 전의 데이터를 읽어온다. 이런 특징은 트랜잭션 원자성을 보장하기에 적합하다 생각했다.

## 2.4 물리적 외래키(FK)

→ **물리적 외래키를 걸지 않는다. 외래키 관계가 있을 땐 참조값만 DB에 넣어 사용한다.**

- [물리적 FK를 사용하지 않는 이유](https://github.com/xiu0327/2023-numble-banking/wiki/%F0%9F%8C%95-%EB%AC%BC%EB%A6%AC%EC%A0%81-FK%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94-%EC%9D%B4%EC%9C%A0)

# 3. API 스펙

→ **[HTTP API 설계 원칙](https://github.com/yoondo/http-api-design/tree/master/ko#%EC%98%AC%EB%B0%94%EB%A5%B8-%EC%83%81%ED%83%9C-%EC%BD%94%EB%93%9C%EB%A5%BC-%EB%B0%98%ED%99%98%ED%95%98%EB%9D%BC)을 준수한다.**

흔히 REST API를 사용하고 있다 하지만, 실제 REST API의 설계 원칙을 모두 준수하여 개발하는 사람은 드물다. REST API는 HATEOAS라는 원칙을 지켜야 하는데, 이를 지키기 쉽지 않기 때문이다. HATEOAS는 응답 본문에 애플리케이션 상태 전이가 가능한 경우를 명시해야 한다. 그러나 현재 프로젝트의 API 직렬화 포맷은 Json이기 때문에 응답 본문에 상태 전이를 표시하기 힘들다. 또한 REST API의 설계 원칙을 따르지 않아도 현재 요구사항을 만족하는 API를 충분히 만들 수 있다.

# 4. 코드 설계

### 4.1 코딩 표준

- [코딩 표준](https://github.com/xiu0327/2023-numble-banking/wiki/%EC%BD%94%EB%94%A9-%ED%91%9C%EC%A4%80)

### 4.2 연관매핑

→  **애너테이션을 활용한 연관매핑은 최대한 지양한다.**

- [연관매핑을 지양하는 이유](https://github.com/xiu0327/2023-numble-banking/wiki/%EC%97%B0%EA%B4%80%EB%A7%A4%ED%95%91%EC%9D%84-%EC%A7%80%EC%96%91%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0)

### 4.3 테스트 코드 목록

- [테스트 목록](https://github.com/xiu0327/2023-numble-banking/wiki/%ED%85%8C%EC%8A%A4%ED%8A%B8-%EB%AA%A9%EB%A1%9D)

# 5. 트랜잭션 원자성 보장 및 동시성 문제 해결

→  **@Transaction과 낙관적 락을 이용하여 동시성 해결.**

- [분명 돈이 있었는데요 없었습니다](https://github.com/xiu0327/2023-numble-banking/wiki/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%9B%90%EC%9E%90%EC%84%B1-%EB%B0%8F-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)

# 6. 거래량에 대한 고찰

→ 거래량이 높은 친구 순으로 친구 목록 조회 해보기

- [거래량에 대한 고찰](https://github.com/xiu0327/2023-numble-banking/wiki/%EA%B1%B0%EB%9E%98%EB%9F%89%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EC%B0%B0)
