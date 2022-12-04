package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
/**
 * properties = {"spring.aop.proxy-target-class=false"} :
 * application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다.
 * 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용\
 * application.properties 에 spring.aop.proxytarget-class 관련 설정이 없어야 한다.
 *
 * JDK 동적 프록시, 인터페이스에만 의존, 구체 클래스 불가
 * @SpringBootTest(properties = {"spring.aop.proxy-target-class=false"})
 *
 * CGLIB 프록시
 * @SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})
 *
 *
 * @SpringBootTest : 내부에 컴포넌트 스캔을 포함
 */
@SpringBootTest
@Import(ProxyDIAspect.class)
public class ProxyDITest {


    /**
     * MemberServiceImpl 에 @Component 가 붙어있으므로 스프링 빈 등록 대상
     *
     * CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     * MemberServiceImpl 은 MemberService 인터페이스를 구현했기 때문에 해당 타입으로 캐스팅 할 수 있다.
     * MemberService = CGLIB Proxy 가 성립
     */
    @Autowired
    MemberService memberService;

    /**
     * JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다.
     * 따라서 MemberServiceImpl 타입이 뭔지 전혀 모른다.
     * 그래서 해당 타입에 주입할 수 없다
     *
     * CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     * 따라서 해당 타입으로 캐스팅 할 수 있다
     */
    @Autowired
    MemberServiceImpl memberServiceImpl;

    @Test
    void go() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }
}
/**
 * 정리
 * JDK 동적 프록시는 대상 객체인 MemberServiceImpl 타입에 의존관계를 주입할 수 없다.
 * CGLIB 프록시는 대상 객체인 MemberServiceImpl 타입에 의존관계 주입을 할 수 있다.
 * 지금까지 JDK 동적 프록시가 가지는 한계점을 알아보았다. 실제로 개발할 때는 인터페이스가 있으면
 * 인터페이스를 기반으로 의존관계 주입을 받는 것이 맞다.
 * DI의 장점이 무엇인가? DI 받는 클라이언트 코드의 변경 없이 구현 클래스를 변경할 수 있는 것이다. 이렇게
 * 하려면 인터페이스를 기반으로 의존관계를 주입 받아야 한다. MemberServiceImpl 타입으로 의존관계
 * 주입을 받는 것 처럼 구현 클래스에 의존관계를 주입하면 향후 구현 클래스를 변경할 때 의존관계 주입을
 * 받는 클라이언트의 코드도 함께 변경해야 한다.
 * 따라서 올바르게 잘 설계된 애플리케이션이라면 이런 문제가 자주 발생하지는 않는다.
 * 그럼에도 불구하고 테스트, 또는 여러가지 이유로 AOP 프록시가 적용된 구체 클래스를 직접 의존관계 주입
 * 받아야 하는 경우가 있을 수 있다. 이때는 CGLIB를 통해 구체 클래스 기반으로 AOP 프록시를 적용하면
 * 된다.
 * 여기까지 듣고보면 CGLIB를 사용하는 것이 좋아보인다. CGLIB를 사용하면 사실 이런 고민 자체를 하지
 * 않아도 된다. 다음 시간에는 CGLIB의 단점을 알아보자
 *
 * CGLIB는 생성자 2번 호출됨
 *
 * 타겟인 구현 클래스 생성자 호출 1번 프록시에서 부모 생성자를 호출하니 그때 또 1번
 * 그러니까 프록시가 프록시 만들 때
 *
 * 정리
 * JDK 동적 프록시는 대상 클래스 타입으로 주입할 때 문제가 있고, CGLIB는 대상 클래스에 기본 생성자
 * 필수, 생성자 2번 호출 문제
 *
 *
 * 스프링의 기술 선택 변화
 *
 * 스프링 3.2, CGLIB를 스프링 내부에 함께 패키징
 *  CGLIB를 사용하려면 CGLIB 라이브러리가 별도로 필요했다. 스프링은 CGLIB 라이브러리를 스프링
 * 내부에 함께 패키징해서 별도의 라이브러리 추가 없이 CGLIB를 사용할 수 있게 되었다. CGLIB springcore org.springframework
 *
 * CGLIB 기본 생성자 필수 문제 해결
 *
 * 스프링 4.0부터 CGLIB의 기본 생성자가 필수인 문제가 해결되었다.
 * objenesis 라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성이 가능하다.
 * 참고로 이 라이브러리는 생성자 호출 없이 객체를 생성할 수 있게 해준다.
 *
 * 생성자 2번 호출 문제
 *
 * 스프링 4.0부터 CGLIB의 생성자 2번 호출 문제가 해결되었다.
 * 이것도 역시 objenesis 라는 특별한 라이브러리 덕분에 가능해졌다.
 * 이제 생성자가 1번만 호출된다.
 * 타겟을 호출 할 때만 생성자 호출되는
 *
 * 스프링 부트 2.0 - CGLIB 기본 사용
 *
 * 스프링 부트 2.0 버전부터 CGLIB를 기본으로 사용하도록 했다.
 * 이렇게 해서 구체 클래스 타입으로 의존관계를 주입하는 문제를 해결했다.
 * 스프링 부트는 별도의 설정이 없다면 AOP를 적용할 때 기본적으로 proxyTargetClass=true 로 설정해서 사용한다.
 * 따라서 인터페이스가 있어도 JDK 동적 프록시를 사용하는 것이 아니라 항상 CGLIB를 사용해서 구체클래스를 기반으로 프록시를 생성한다.
 * 물론 스프링은 우리에게 선택권을 열어주기 때문에 다음과 깉이 설정하면 JDK 동적 프록시도 사용할 수 있다.
 * application.properties
 * spring.aop.proxy-target-class=false
 *
 * 정리
 * 스프링은 최종적으로 스프링 부트 2.0에서 CGLIB를 기본으로 사용하도록 결정했다.
 * CGLIB를 사용하면 JDK 동적 프록시에서 동작하지 않는 구체 클래스 주입이 가능하다.
 * 여기에 추가로 CGLIB의 단점들이 이제는 많이 해결되었다.
 * CGLIB의 남은 문제라면 final 클래스나 final 메서드가 있는데,
 * AOP를 적용할 대상에는 final 클래스나 final 메서드를 잘 사용하지는 않으므로 이 부분은 크게 문제가 되지는 않는다.
 * 개발자 입장에서 보면 사실 어떤 프록시 기술을 사용하든 상관이 없다.
 * JDK 동적 프록시든 CGLIB든 또는 어떤 새로운 프록시 기술을 사용해도 된다.
 * 심지어 클라이언트 입장에서 어떤 프록시 기술을 사용하는지 모르고 잘 동작하는 것이 가장 좋다.
 * 단지 문제 없고, 개발하기에 편리하면 되는 것이다.
 * 마지막으로 ProxyDITest 를 다음과 같이 변경해서 아무런 설정 없이 실행해보면 CGLIB가 기본으로 사용되는 것을 확인
 * */