package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 강제 초기화 : orderItems 컬렉션과 orderItem 의 name 을 출력
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(v -> v.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<OrderDto> collect = orders.stream()
                .map(v -> new OrderDto(v))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllByFetchJoin();


        for (Order order : orders) {
            System.out.println("order ref = " + order + " id=" + order.getId());
        }
        
        List<OrderDto> collect = orders.stream()
                .map(v -> new OrderDto(v))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * 방법 1 application.yml 에  default_batch_fetch_size: 100  추가 (in 절의 갯수)
     * 방법 2 Entity 객체에 @BatchSize(size = 1000) 추가
     *       ex) @BatchSize(size = 1000)
     *           private List<OrderItem> orderItems new ArrayList<>();
     * -> in 절로 바뀜 (1->N->M) ----> (1->1->1) 로 개선
     * -> 1. orders, member, delivery 은 join 쿼리
     * -> 2. order_item 은 in 절 쿼리
     * -> 3. items 은 in 절 쿼리
     * -> 페이징도 되면서 성능최적화도 가능
     * to One 관계는 join fetch 로 하고  on to many 관계는 default_batch_fetch_size 설정하여 in 절로 조회하게 한다.
     *
     * */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findOrderByFetchJoin(offset, limit);

        for (Order order : orders) {
            System.out.println("order ref = " + order + " id=" + order.getId());
        }

        List<OrderDto> collect = orders.stream()
                .map(v -> new OrderDto(v))
                .collect(Collectors.toList());

        return collect;
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime localDate;
        private OrderStatus orderStatus;
        private Address address;
        // DTO 안에 ENTITY 가 있으면 안된다. ENTITY 에대한 의존을 없애야한다. orderItem 도 dto 로 변환해야 한다.
        // private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            this.orderId = o.getId();
            this.name = o.getMember().getName();
            this.localDate = o.getOrderDate();
            this.address = o.getDelivery().getAddress();
            // o.getOrderItems().stream().forEach(v -> v.getItem().getName()); // 초기화
            // this.orderItems = o.getOrderItems();
            this.orderItems = o.getOrderItems().stream()
                    .map(v -> new OrderItemDto(v))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

}
