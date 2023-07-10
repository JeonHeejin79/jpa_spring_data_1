package jpabook.jpashop;

/**
 * [엔티티 설계시 주의점]
 *
 * - 엔티티에는 가급적 Setter 를 사용하지 말자
 * - 모든 연관관계는 지연로딩으로 설정, 실무에서 모든 연관관계는 지연로딩 LAZY 으로 설정해야 한다.
 *   ㄴ JPQL 을 실행할떄 N+1 문제가 자주 발생한다.
 *   ㄴ @ManyToOne : 기본패치전략 fetch = FetchType.EAGER -> 반드시 FetchType.LAZY 로 설정
 *   ㄴ @OneToOne : 기본패치전략 fetch = FetchType.EAGER -> 반드시 FetchType.LAZY 로 설정
 *   ㄴ @OneToMany : 기본패치전략 fetch = FetchType.LAZY
 * - 컬렉션은 필수 초기화 하자 : 초기화 후 변경하지 말자
 * - 테이블 컬럼명 생성 전략 : 카멜케이스 ->  언더스코어 (memberPoint -> member_point)
 * - CascadeType.ALL :
 *     persist(orderItemA)
 *     persist(orderItemB)   --->   persist(order) 한번으로 모든 persist를 같이해준다.
 *     persist(orderItemC)
 *     persist(order)
 * - 양방향 연관관계 편의메서드
 * ======================================================================================
 * 쿠현요구사항
 *  회원 (등록 조회) / 상품 (등록 수정 조회) / 주문 (등록 취소 조회)
 *
 * 애플리케이셔 아키텍처
 *  Controlelr -> Service -> Repository -> DB
 *        |-------- Domain ---------|
 * 개발순서 : 서비스 레파ㄴ지토리 계층을 개발 -> 테스트케이스 작성 검증 -> 웹 계층 적용
 * 개발 : 회원도메인 -> 상품도메인 -> 주문도메인 -> 웹 계층 개발 -> API 개발 기본 -> API 개발 고급
 * ======================================================================================
 *
 */
public class note {
}
