package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional // 테스트 종료후 데이터를 롤백한다.
    @Rollback(false)
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setName("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        Assertions.assertThat(findMember).isEqualTo(member);
        // findMember == member
        // 같은 영속성 컨텍스트 안에서는 식별자가(id) 같으면 같은 객체라고 보면 된다.
        // 1차캐시에 있는 객체를 불러온다.
        System.out.println(findMember == member);
    }

    @Test
    public void testMember2() throws Exception {
        // given

        // when

        // then

    }
}