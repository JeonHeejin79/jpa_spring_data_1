package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장
    public void save(Member member) {
        em.persist(member);
    }

    // 조회
    public Member findOne(Long id) {
        Member member = em.find(Member.class, id);
        return member;
    }
}
