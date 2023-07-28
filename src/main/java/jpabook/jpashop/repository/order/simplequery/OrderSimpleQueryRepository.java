package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

// 바포 dto 로 조회하는경우 분리하는게 좋다.
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

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
}
