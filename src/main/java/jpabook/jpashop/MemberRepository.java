package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em; // 스프링이 starter-data-jap 로 EntityManager 을 생성해서 빈으로 등록해 놓는다.

    public Long save(Member member) {
        em.persist(member); // 저장을 하고나면 리턴을 안만든다. 조회와 저장을 분리 하기 위함
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
