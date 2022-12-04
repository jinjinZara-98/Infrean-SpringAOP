package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
 * target : Target 객체(스프링 AOP 프록시가 가르키는 실제 대상) 를 대상으로 하는 조인 포인트
 *
 * this , target 은 다음과 같이 적용 타입 하나를 정확하게 지정
 * * 같은 패턴을 사용할 수 없다. 부모 타입을 허용
 *
 * 스프링에서 AOP를 적용하면 실제 target 객체 대신에 프록시 객체가 스프링 빈으로 등록된다.
 * this 는 스프링 빈으로 등록되어 있는 프록시 객체를 대상으로 포인트컷을 매칭한다.
 * target 은 실제 target 객체를 대상으로 포인트컷을 매칭한다
 *
 * JDK 프록시로 했을 때 this target 에 구현체 넣으면 this는 AOP 적용 대상 아님
 * 인터페이스를 사용해 프록시 생성하므로 빈으로 등록된건 인터페이스를 구현한 아예 다른 클래스
 * target은 실제 객체가 구현체 타입이므로  AOP 적용 대상
 *
 * application.properties
 * spring.aop.proxy-target-class=true  CGLIB
 * spring.aop.proxy-target-class=false JDK 동적 프록시
 *
 * this , target 은 실제 객체를 만들어야 테스트 할 수 있다.
 * 테스트에서 스프링 컨테이너를 사용해서 target , proxy 객체를 모두 만들어서 테스트해보자.
 * properties = {"spring.aop.proxy-target-class=false"} :
 * application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다.
 * 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
 */
@Slf4j
//밑에 어드바이저인 @Aspect가 붙은 클래스 빈 등록
@Import(ThisTargetTest.ThisTargetAspect.class)

/**
 * JDK 동적 프록시를 우선 생성, . 물론 인터페이스가 없다면 CGLIB를 사용
 *
 * application.properties 에 spring.aop.proxytarget-class 관련 설정이 없어야 한다.
 * */
@SpringBootTest(properties = "spring.aop.proxy-target-class=false")

/**
 * 스프링이 AOP 프록시를 생성할 때 CGLIB 프록시를 생성한다.
 * 참고로 이 설정을 생략하면 스프링 부트에서 기본으로 CGLIB를 사용
 * */
//@SpringBootTest(properties = "spring.aop.proxy-target-class=true")
public class ThisTargetTest {

    //자동 의존 주입, 프록시 객체가 빈으로 등록되었으니 프록시 객체가 주입됨
    //JDK 동적 프록시는 MemberService 인터페이스 구현해서 생성한 프록시 주입
    //CGLIB 는 MemberServiceImpl 구체 클래스를 상속받아서 생성한 프록시 주입
    @Autowired
    MemberService memberService;

    @Test
    void success() {
        //프록시 객체인지 확인
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {

        /**
         * 부모 타입 허용, this 사용, 타입 하나 제대로 지정
         *
         * JDK 동적 프록시: MemberService 인터페이스 구현한 프록시와 MemberService 매칭
         * CGLIB: MemberServiceImpl 구체클래스 상속해서 만든 프록시와 MemberService 매칭
         * */
        @Around("this(hello.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            //this 는 스프링 컨테이너에 등록된 프록시 빈과 매칭

            //joinPoint.getSignature() 실제 객체 정보 출력
            //JDK 동적 프록시면 인터페이스인 MemberService 구현해서 프록시 생성
            //CGLIB 는 구체 클래스인 MemberServiceImpl 상속해서 프록시 생성
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /** 부모 타입 허용, target 사용, 타입 하나 제대로 지정
         *
         * JDK 동적 프록시: 실제 객체 MemberService 와  MemberService 매칭
         * CGLIB: 실제 객체 MemberServiceImpl 과 MemberService 매칭
         * */
        @Around("target(hello.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            //target 은 프록시 빈이 아닌 실제 객체와 매칭

            //joinPoint.getSignature() 실제 객체 정보 출력
            //JDK 동적 프록시면 인터페이스인 MemberService 구현해서 프록시 생성
            //CGLIB 는 구체 클래스인 MemberServiceImpl 상속해서 프록시 생성
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * 구체 대상, this 사용
         * JDK 프록시 사용하면 이거만 호출 안되는
         *
         * JDK 동적 프록시: MemberService 인터페이스 구현한 프록시와 MemberServiceImpl 매칭
         * MemberService 인터페이스 구현한 프록시와 MemberServiceImpl 은 아예 다른 객체 매칭 안됨
         * JDK 동적 프록시 사용하면 이 포인트컷 조건 안맞아 부가 기능 적용 안됨
         *
         * CGLIB: MemberServiceImpl 구체클래스 상속해서 만든 프록시와 MemberServiceImpl 매칭
         * */
        @Around("this(hello.aop.member.MemberServiceImpl)")
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            //this 는 스프링 컨테이너에 등록된 프록시 빈과 매칭

            //joinPoint.getSignature() 실제 객체 정보 출력
            //JDK 동적 프록시면 인터페이스인 MemberService 구현해서 프록시 생성
            //CGLIB 는 구체 클래스인 MemberServiceImpl 상속해서 프록시 생성
            log.info("[this-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * target: 실제 target 객체 대상
         *
         * JDK 동적 프록시: 실제 객체 MemberService 와 MemberServiceImpl 매칭
         * CGLIB: 실제 객체 MemberServiceImpl 과 MemberServiceImpl 매칭
         * */
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            //target 은 프록시 빈이 아닌 실제 객체와 매칭

            //joinPoint.getSignature() 실제 객체 정보 출력
            //JDK 동적 프록시면 인터페이스인 MemberService 구현해서 프록시 생성
            //CGLIB 는 구체 클래스인 MemberServiceImpl 상속해서 프록시 생성
            log.info("[target-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}