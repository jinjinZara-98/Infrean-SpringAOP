package hello.aop.order.aop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 이제 OrderService , OrderRepository 의 모든 메서드는 AOP 적용의 대상이 된다.
 *
 * 참고로 스프링은 프록시 방식의 AOP를 사용하므로 프록시를 통하는 메서드만 적용 대상이 된다.
 *
 * 스프링 AOP는 AspectJ의 문법을 차용하고, 프록시 방식의 AOP를 제공한다. AspectJ를 직접 사용하는 것이 아니다.
 * 스프링 AOP를 사용할 때는 @Aspect 애노테이션을 주로 사용하는데, 이 애노테이션도 AspectJ가 제공하는 애노테이션이다
 *
 * @Aspect 를 포함한 org.aspectj 패키지 관련 기능은 aspectjweaver.jar 라이브러리가 제공하는 기능이다.
 * 앞서 build.gradle 에 spring-boot-starter-aop 를 포함했는데,
 * 이렇게 하면 스프링 AOP 관련 기능과 함께 aspectjweaver.jar 도 함께 사용할 수 있게 의존 관계에 포함된다.
 * 그런데 스프링에서는 AspectJ가 제공하는 애노테이션이나 관련 인터페이스만 사용하는 것이고,
 * 실제 AspectJ가 제공하는 컴파일, 로드타임 위버 등을 사용하는 것은 아니다.
 * 스프링은 지금까지 우리가 학습한 것 처럼 프록시 방식의 AOP를 사용한다
 *
 * @Aspect 는 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다.
 * 따라서 AspectV1 를 AOP로 사용하려면 스프링 빈으로 등록해야 한다.
 * 스프링 빈으로 등록하는 방법은 다음과 같다.
 * @Bean 을 사용해서 직접 등록
 * @Component 컴포넌트 스캔을 사용해서 자동 등록
 * @Import 주로 설정 파일을 추가할 때 사용( @Configuration )
 * @Import 는 주로 설정 파일을 추가할 때 사용하지만, 이 기능으로 스프링 빈도 등록할 수 있다.
 *
 * 테스트에서는 버전을 올려가면서 변경할 예정이어서 간단하게 @Import 기능을 사용하자
 * */
@Slf4j
@Aspect
public class AspectV1 {

    //포인트컷, hello.aop.order 패키지와 하위 패키지
    /** 모든 반환 타입, 패키지 지정, 모든 메서드 이름, 파라미터 상관없음 */
    @Around("execution(* hello.aop.order..*(..))")
    //어드바이스
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        //join point 시그니처, 어떤 클래스의 메서드가 실행되었는지
        log.info("[log] {}", joinPoint.getSignature());

        //실제 타겟 호출
        return joinPoint.proceed();
    }
}
