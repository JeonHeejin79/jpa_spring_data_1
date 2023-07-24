package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회할때 성능을 더 최적화 한다.
@RequiredArgsConstructor // final 로 선언된 필드로 생성자를 만들어준다.
public class MemberService {

    // 생성자 Injection 이 좋다. (filed injection, setter injection, constructor injection)
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional // 쓰기에는 resOnlyTrue 를 넣지 않는다.
    public Long join(Member member) {
        validationDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validationDuplicateMember(Member member) {
        // EXCEPTION
        // 멀티스레드 환경에서 동시 접근을 방어하기위해 db 에 name 을 유니크제약조건을 걸어두는게 좋다.
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        // 커맨드와 쿼리를 분리한다.
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
