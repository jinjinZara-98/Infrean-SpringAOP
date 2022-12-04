package hello.aop.pointcut;

import hello.aop.member.annotation.ClassAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
/**
 * ClassAop 이용
 *
 * @target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
 * @within : 주어진 애노테이션이 있는 타입 내 조인 포인트
 *
 * target은 인스턴스 기준으로 하고 걸리면 가지고 있는 모든 메서드 대상
 * within은 정적인 클래스 정보, 부모 타입 안되고 내꺼만 적용됨
 */
@Slf4j
//import해줘 스프링 뜰 때 빈들이 다 등록됨
@Import({AtTargetAtWithinTest.Config.class})
@SpringBootTest
public class AtTargetAtWithinTest {

    //자동 의존 주입
    @Autowired
    Child child;

    @Test
    void success() {
        log.info("child Proxy={}", child.getClass());
        child.childMethod(); /** 부모, 자식 모두 있는 메서드 */
        child.parentMethod(); /** 부모 클래스만 있는 메서드 */
    }

    //빈으로 등록하기 위해 만듬
    static class Config {

        @Bean
        public Parent parent() {
            return new Parent();
        }

        @Bean
        public Child child() {
            return new Child();
        }

        //어드바이저인 @Aspect 붙은 클래스 비느올 등록
        @Bean
        public AtTargetAtWithinAspect atTargetAtWithinAspect() {
            return new AtTargetAtWithinAspect();
        }
    }

    static class Parent {
        public void parentMethod(){} //부모에만 있는 메서드
    }

    //자식에 @ClassAop 붙임
    @ClassAop
    static class Child extends Parent {
        public void childMethod(){}
    }

    /**
     * 이전의 포인트컷처럼 편하게 테스트 불가
     * 동적인 객체 인스턴스가 실행이 되어야함
     * 직접 에스펙트로 다 만들고 스프링 컨테이너로 돌려야함
     * */
    @Slf4j
    @Aspect
    static class AtTargetAtWithinAspect {

        /**
         * @target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정, 부모 타입의 메서드도 적용
         * ClassAop가 있는 child가 걸림 Parent까지
         * execution(* hello.aop..*(..)) 없으면 안됨
         *
         * aop패키지와 그 하위 패키지 메서드 이름 상관없고 모든 파라미터 허용
         *
         * @target(hello.aop.member.annotation.ClassAop)으로 @ClassAop있는 클래스의 메서드와 그 부모의 메서드까지
         * */
        @Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")
        public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@target] {}", joinPoint.getSignature());

            return joinPoint.proceed();
        }

        /**
         * Parent는 안됨
         * @within: 선택된 클래스 내부에 있는 메서드만 조인 포인트로 선정, 부모 타입의 메서드는 적용되지 않음
         * */
        @Around("execution(* hello.aop..*(..)) && @within(hello.aop.member.annotation.ClassAop)")
        public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@within] {}", joinPoint.getSignature());

            return joinPoint.proceed();
        }
    }
}
/**
 * 다음 포인트컷 지시자는 단독으로 사용하면 안된다. args, @args, @target
 * 이번 예제를 보면 execution(* hello.aop..*(..)) 를 통해 적용 대상을 줄여준 것을 확인할 수 있다.
 * args , @args , @target 은 실제 객체 인스턴스가 생성되고 실행될 때 어드바이스 적용 여부를 확인할 수 있다.
 * 실행 시점에 일어나는 포인트컷 적용 여부도 결국 프록시가 있어야 실행 시점에 판단할 수 있다.
 * 프록시가 없다면 판단 자체가 불가능하다.
 * 그런데 스프링 컨테이너가 프록시를 생성하는 시점은 스프링 컨테이너가 만들어지는 애플리케이션 로딩 시점에 적용할 수 있다.
 * 따라서 args , @args , @target 같은 포인트컷 지시자가 있으면 스프링은 모든 스프링 빈에 AOP를 적용하려고 시도한다.
 * 앞서 설명한 것 처럼 프록시가 없으면 실행 시점에 판단 자체가 불가능하다.
 * 문제는 이렇게 모든 스프링 빈에 AOP 프록시를 적용하려고 하면 스프링이 내부에서 사용하는 빈 중에는
 * final 로 지정된 빈들도 있기 때문에 오류가 발생할 수 있다.
 * 따라서 이러한 표현식은 최대한 프록시 적용 대상을 축소하는 표현식과 함께 사용
 * */