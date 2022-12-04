package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CGLIB 와 JDK 프록시 , 인터페이스와 구체 클래스로 타입 캐스팅 테스트
 *
 * 스프링이 프록시를 만들때 제공하는 ProxyFactory 에 proxyTargetClass 옵션에 따라
 * 둘중 하나를 선택해서 프록시를 만들 수 있다.
 * proxyTargetClass=false JDK 동적 프록시를 사용해서 인터페이스 기반 프록시 생성
 * proxyTargetClass=true CGLIB를 사용해서 구체 클래스 기반 프록시 생성
 * 참고로 옵션과 무관하게 인터페이스가 없으면 JDK 동적 프록시를 적용할 수 없으므로 CGLIB를 사용한다.
 *
 * JDK 동적 프록시 한계
 * 인터페이스 기반으로 프록시를 생성하는 JDK 동적 프록시는 구체 클래스로 타입 캐스팅이 불가능한
 * 한계가 있다. 어떤 한계인지 코드를 통해서 알아보자
 * */
@Slf4j
public class ProxyCastingTest {

    @Test
    void jdkProxy() {
        //구체클래스도 있고 인터페이스도 있는
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(false); //JDK 동적 프록시

        //프록시를 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        /**
         * JDK 동적 프록시는 인터페이스를 기반으로 프록시를 생성하기 때문이다.
         * JDK Proxy는 MemberService 인터페이스를 기반으로 생성된 프록시이다.
         * 따라서 JDK Proxy는 MemberService 로 캐스팅은 가능하지만 MemberServiceImpl 이 어떤 것인지 전혀 알지 못한다
         * JDK 동적 프록시를 구현 클래스로 캐스팅 시도 실패, ClassCastException 예외 발생
         * MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy 이 코드만 적고 실행하면
         * ClassCastException.class이 예외 터짐
         */
        assertThrows(ClassCastException.class, () -> {
            MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
        });
    }


    /**
     * MemberServiceImpl 타입을 기반으로 CGLIB 프록시를 생성했다.
     * MemberServiceImpl 타입은 MemberService 인터페이스를 구현했다.
     * CGLIB는 구체 클래스를 기반으로 프록시를 생성한다.
     * 따라서 CGLIB는 MemberServiceImpl 구체 클래스를 기반으로 프록시를 생성한다.
     * 이 프록시를 CGLIB Proxy라고 하자. 여기서 memberServiceProxy 가 바로 CGLIB Proxy
     *
     * 여기에서 CGLIB Proxy를 대상 클래스인 MemberServiceImpl 타입으로 캐스팅하면 성공한다.
     * 왜냐하면 CGLIB는 구체 클래스를 기반으로 프록시를 생성하기 때문이다.
     * CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 생성된 프록시이다.
     * 따라서 CGLIB Proxy는 MemberServiceImpl 은 물론이고,
     * MemberServiceImpl 이 구현한 인터페이스인 MemberService 로도 캐스팅 할 수 있다.
     */
    @Test
    void cglibProxy() {

        /**
         * cglibProxy는 MemberServiceImpl을 상속받아서 만들었음, 부모타입으로 캐스팅 가능하므로
         * @Autowired MemberService memberService : CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
         * MemberServiceImpl 은 MemberService 인터페이스를 구현했기 때문에 해당타입으로 캐스팅 할 수 있다
         */
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true); //CGLIB 프록시

        //프록시를 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        log.info("proxy class={}", memberServiceProxy.getClass());

        //CGLIB 프록시를 구현 클래스로 캐스팅 시도 성공
        MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
    }
}
