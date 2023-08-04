package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // 스프링부트가 지원해준다.
public class MemberRepositoryOld {

//    @PersistenceContext
//    private EntityManager em;

    private final EntityManager em;

    // 저장
    public void save(Member member) {
        em.persist(member);
    }

    // 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 리스트 조회 - jpql 사용
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
