package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // JPQL
    public List<Order> findAll(OrderSearch orderSerarch) {
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status" +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSerarch.getOrderStatus())
                .setParameter("name", orderSerarch.getMemberName())
                // .setFirstResult(100) 페이징
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    // fetch join 패치조인 (적극적으로 홀요해야 한다.)
    // 재사용성이 높다. -> 원하는 dto 에 transfer 해서 사용 가능하다.
    public List<Order> findOrderByFetchJoin() {
        // lazy 를 무시하고 객체에 다 값을 채워서 가져온다.
        // select 절에서 모든것을 가져온다.
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class).getResultList();
    }

    // jpa 에서 dto 로 바로조회
    // 재사용이 안된다. dto 로 조회했기 떄문에 변경이 안된다.
    // 성능 최적화면에서 조금 더 낳다.
    // 코드상 조금 더 지저분하다.
    public List<OrderSimpleQueryDto> findOrderDtos() {

        // ENTITY 만 반환가능
        // DTO 반환하려면 NEW 해줘야함
        // select 절에서 원하는것만 가져올 수 있음
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class).getResultList();
    }

    public List<Order> findAllByFetchJoin() {

        // distinct 가 잇으면 order 가 같은 값이 있으면 중복을 제거해준다.
        // 쿼리상으로는 안된다. 객체상으로만 중복제가가 된다.
        // 단점 : 페이징쿼리가 안된다. (setFirstResult, setMaxResults) 이 적용 안된다.
        // 1:N 조인데엇는 패치조인을 쓰면 안된다. ROW 수가 증가된 페이징 처리가 된다.
        // 모든 데이터를 DB 에서 읽어오고 메모리에서 페이징 처리 해버린다.
        // + 컬렉션 페치 조인은 1개만 사용할 수 있다. 둘 이상 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다.
        return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
            // .setFirstResult(1) // 첫번쨰부터 시작
            // .setMaxResults(100) // 100 개까지 조회
            .getResultList();
    }

    public List<Order> findOrderByFetchJoin(int offset, int limit) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();

    }
}
