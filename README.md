# 1. 프로젝트 목표

1. 객체지향 원리를 이해하고 클린 코드 규칙을 준수하여 올바른 코드를 작성합니다.
2. 계좌이체 시 발생 가능한 동시성 문제를 해결하고 트랜잭션 원자성을 보장합니다.
3. 단순한 기능 구현 뿐만 아니라 대용량 트래픽까지 고려한 뱅킹 서버를 구축합니다.
4. 친구 목록 조회 API의 요구사항을 추가하여 기능을 추가해보며 코드 유지보수성이 좋은지 테스트해봅니다.

# 2. RDB

## 2.1 RDB vs NoSQL

→ **도메인 특성 상 스키마가 변경되면 안되기 때문에 RDB를 사용한다.**

RDB와 NoSQL의 가장 큰 특징은 유연성이라 생각한다. RDB는 스키마가 고정되어있지만, NoSQL은 스키마가 유동적이다. 하지만 현재 프로젝트의 DB는 스키마가 유연해선 안된다. 특히 계좌 스키마가 유연하다면 돈을 넣었는데 돈이 없다고 하는 큰 장애가 일어날 수 있다. 따라서 프로젝트의 DB는 NoSQL이 아닌 RDB를 사용하며, RDB 중에서도 MySQL을 사용한다.

## 2.2 MySQL

→ **MySQL의 innoDB는 트랜잭션의 원자성과 동시성 문제를 해결해줄 장점을 지닌다.**

MySQL은 innoDB라는 스토리지 엔진을 사용한다. innoDB 엔진은 여러 장점을 지니고 있는데, 특히 innoDB 엔진은 과제에서 요구하는 트랜잭션 원자성 보장과 동시성 문제를 잘 해결해준다. innoDB 엔진은 레코드 기반의 잠금을 제공하며, 그 때문에 높은 동시성 처리가 가능하고 안정적이며 성능이 뛰어나다. 또한 innoDB 엔진은 MVCC 기술을 이용하여 잠금을 걸지 않고 읽기 작업을 수행한다. 간단하게 말해 다른 트랜잭션이 가진 잠금을 기자리지 않고 읽기 작업이 가능하다.

예를 들어, Member 테이블 하나의 컬럼을 UPDATE 하는 트랜잭션이 들어왔다. 이때 COMMIT 되지 않은 상태에서 SELECT 쿼리문이 들어오면, 별다른 대기 없이 바로 실행된다. innoDB 트랜잭션 격리 수준은 READ COMMIT이 디폴트 값이기 때문에 변경되기 전의 데이터를 읽어온다. 이런 특징은 트랜잭션 원자성을 보장하기에 적합하다 생각했다.

## 2.3 물리적 외래키(FK)

→ **물리적 외래키를 걸지 않는다. 외래키 관계가 있을 땐 참조값만 DB에 넣어 사용한다.**

![erd.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/49f7ee62-9969-4c0b-a95b-79fc0dbc3acf/erd.png)

이번 프로젝트를 진행하며 DB에 물리적 FK를 걸지 않기로 했다. ERD에는 관계를 표현하기 위해 FK를 표시해뒀을 뿐, 실제 DB엔 물리적 FK가 걸려있지 않다.

지금껏 나는 줄곧 물리적 FK를 사용해왔다. 하지만 프로젝트를 몇번 만들어보니까 물리적 FK가 불편하다는 걸 깨달았다.

1. 성능 문제

   물리적 FK를 걸면 DB에서 테이블 간의 무결성을 보장하기 위해 제약조건을 설정한다. 그래서 연관관계가 복잡해지면 쿼리에 추가해야할 리소스가 늘어났다. 이는 성능을 하락시킬 수 있다.

2. 데이터 삭제 및 갱신 문제

   물리적 FK를 걸면 부모 테이블의 레코드가 삭제되거나 업데이트될 때 자식 테이블의 레코드도 함께 갱신되거나 삭제된다. 하지만 이런 경우 부모 테이블이나 자식 테이블 중 하나에서 오류가 발생할 경우 데이터 일관성이 깨질 수 있다. 그래서 FK가 걸린 부모 엔티티 객체를 삭제하려고 하면 JPA는 컴파일 오류를 발생시킨다.

3. 데이터 베이스 디자인 제한

   FK는 테이블 간 관계를 명확하게 정의하는 데 사용된다. 하지만 데이터 베이스를 설계할 때, 다양한 요구사항에 따라 관계 유형을 설정할 수 있다. 하지만 물리적 FK를 건다면 이런 유연성을 제한한다.


```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
		name = "owner_id", 
		foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
private Member owner;
```

따라서 이번 프로젝트를 진행할 때, 연관매핑을 사용해도 `@ForeignKey` 어노테이션을 사용하여 실제 DB엔 외래키가 걸리지 않도록 했다.

# 3. API 스펙

→ [**HTTP API 설계 원칙](https://github.com/yoondo/http-api-design/tree/master/ko#%EC%98%AC%EB%B0%94%EB%A5%B8-%EC%83%81%ED%83%9C-%EC%BD%94%EB%93%9C%EB%A5%BC-%EB%B0%98%ED%99%98%ED%95%98%EB%9D%BC)을 준수한다.**

흔히 REST API를 사용하고 있다 하지만, 실제 REST API의 설계 원칙을 모두 준수하여 개발하는 사람은 드물다. REST API는 HATEOAS라는 원칙을 지켜야 하는데, 이를 지키기 쉽지 않기 때문이다. HATEOAS는 응답 본문에 애플리케이션 상태 전이가 가능한 경우를 명시해야 한다. 그러나 현재 프로젝트의 API 직렬화 포맷은 Json이기 때문에 응답 본문에 상태 전이를 표시하기 힘들다. 또한 REST API의 설계 원칙을 따르지 않아도 현재 요구사항을 만족하는 API를 충분히 만들 수 있다.

# 5. 코드 설계

## 5.1 코딩 표준

1. 함수형 인터페이스와 람다식을 적극 활용한다.
   1. 코드의 중복을 줄일 수 있기 때문이다.
2. 체크 예외를 사용한다면 반드시 문서화한다.
   1. 개인 프로젝트에선 크게 문제가 되지 않지만 만약 협업을 한다면 예외가 발생할 상황을 정확하게 명시해주는 것이 좋다.
3. try-catch 구문이 길어지거나 중복된 코드가 많다면 @RestControllerAdvice를 사용한다.
   1. 컨트롤러에서 모든 예외를 처리하다 보면 중복된 예외 처리 코드가 발생할 수 있다.
   2. 따라서 예외 처리를 일괄적으로 모아서 관리하면 유지보수가 쉽다.
4. 생성자는 Build 패턴을 사용한다.
   1. 객체가 가진 인자가 많을 경우 인자들이 어떤 값인지 헷갈린다.
   2. 생성자에 정의된 인자를 반드시 채워줘야 하는 불편함이 존재한다.
   3. 따라서 생성자를 빌더패턴으로 생성하면 객체 생성의 유연성을 높일 수 있다.
5. System.out.println으로 디버깅 하지 않는다.
   1. logger를 적극 사용한다.
   2. 각 log는 적절한 level을 지켜 시스템의 불필요한 출력을 줄인다.
   3. System.out.println 대신 logger를 사용하는 이유는 출력이라는 행위 자체가 리소스를 잡아먹기 때문에 서버 성능을 저하시킬 수 있다.
   4. log의 메시지는 문자열 연산을 사용하지 않는다.
6. cross join은 사용하지 않는다. cross join은 곱집합을 사용하기 때문에 성능이 저하된다.
7. Setter를 사용하지 않는다. 무분별한 수정이 일어날 수 있기 때문이다. 로직 상 반드시 필요하다면 setxxx 대신 기능을 나타내는 메서드 명을 사용한다.

## 5.2 연관매핑

→  **애너테이션을 활용한 연관매핑은 최대한 지양**

JPA는 @ManyToOne, @OneToOne과 같은 애너테이션을 이용하여 연관된 객체를 로딩하는 기능을 제공한다. 그래서 애그리거트 루트에 대한 참조를 쉽게 구현하고 필드를 이용한 애그리거트 참조를 사용한다면 개발이 편하고 유지보수하기 쉬워진다.

하지만 필드를 이용한 애그리거트 참조는 **`편한 탐색 오용`**, `성능 문제`, `확장 어려움`을 야기한다.

```java
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @Embedded
    private Transaction transaction;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "friend_id")
    private String friendId;

...
}
```

원래 Friendship 엔티티를 만들 때, owner와 friend 모두 회원이기 때문에 @ManyToOne으로 연관관계 매핑을 맺었다. 하지만 가만 생각해보면 ***Friendship의 주요 기능은 친구 관계를 정의하는 것***이다. 특정 회원의 친구는 누가누가 있는지만 알면 된다. 그래서 굳이 연관 매핑을 맺어서 회원 테이블 필드를 가져올 필요가 있는가 고민되었고, 결국 연관매핑을 제외하고 회원 식별자 값을 넣었다.

이때 회원 일련번호가 아닌 회원 아이디를 넣은 이유는 어차피 클라이언트에서 회원 일련번호가 아닌 회원 아이디를 Authentication 헤더로 전달 받는다. 그래서 굳이 findByUserId 해서 DB에서 회원 일련번호를 가져오지 말고 바로 userId로 Friendship 객체를 생성해서 테이블에 저장하는 게 더 효율적이라 판단했다.

```java
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("select distinct m from Member m inner join Friendship f on m.userId = f.friendId where f.ownerId=: ownerId")
    List<Member> findFriendList(@Param("ownerId") String ownerId, Pageable pageable);

    @Query("select f.friendId from Friendship f where f.ownerId= :ownerId")
    List<String> findAllByOwnerId(@Param("ownerId") String ownerId);
}
```

기존 @OneToMany로 가져오던 친구 목록은 직접 쿼리문을 짜서 가져왔다.

@OneToMany로 가져오던 지금처럼 쿼리문을 짜던 친구 목록을 가져오는 쿼리의 수는 1회로 동일하다. 다만, @OneToMany를 사용하면 mapperBy를 통해 외래키를 가진 엔티티를 정의해줘야 한다. 그럼 친구 관계를 저장할 때 onwer와 friend 회원 엔티티를 findByUserId로 조회해서 가져와야 한다.

반면 식별자값으로 간접참조하면 불러올 필요없이 바로 ownerId와 friendId로 friendship을 DB에 저장하기만 하면 된다. 따라서 전체적으로 보면 3번의 쿼리를 1번으로 줄였다고 볼 수 있다.

![스크린샷 2023-02-25 오후 3.15.36.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/1e30629b-15cc-432e-ad92-34e109cef396/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-02-25_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_3.15.36.png)

실제로 저장 속도를 테스트해보면 연관 매핑 하지 않았을 때가 더 빠르다.

→ **Friendship은 정보를 포함하기 보단 관계를 정의하는 역할이기 때문에 연관매핑을 맺지 않고 owner와 friend의 식별자만 사용했다.**

### 5.2.1 고민

사실 연관매핑에 대한 고민은 끝이 없을 것 같다. 쿼리문을 직접 짜기엔 너무 귀찮다. 간단한 어노테이션 하나면 조인이 이뤄지는데 굳이 싶다. N+1이 문제라면 fetch join으로 불러오면 되지 않을까하는 게으른 생각이 가득하다.

이전 넘블챌린지를 수행하고 코드 리뷰를 받았을 때도, @OneToMany 부분에서 DB I/O 성능 저하가 발생한다고 지적해주셨다.

1. 간단한 어플리케이션은 다르겠지만, 현업에서는 @OneToMany를 최대한 지양한다.
2. 불필요한 N+1 쿼리가 발생하기 때문이다.
3. 개발자가 인지 못할 장애가 발생할 수 있다.

나도 조언을 듣고 연관매핑을 사용해야할 경우엔 “정말 적절한 방법인가?” 한번쯤 생각해보고 쓰게 되었다. 양방향으로 연관관계를 매핑하는 건 어느 정도 불편함을 많이 겪어서 최대한 안 쓰는 방향으로 가는 게 맞는 것 같지만, 단방향은 경우에 따라서 써도 되지 않을까 싶다. 매핑을 쓰는 게 좋은지 안 좋은지 언제나 고민되는 부분이다.