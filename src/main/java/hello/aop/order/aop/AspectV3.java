package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Pointcut 2개 사용
 *
 * 앞서 로그를 출력하는 기능에 추가로 트랜잭션을 적용하는 코드도 추가해보자.
 *
 * 여기서는 진짜 트랜잭션을 실행하는 것은 아니다. 기능이 동작한 것 처럼 로그만
 * 트랜잭션 기능은 보통 다음과 같이 동작한다.
 *
 * 핵심 로직 실행 직전에 트랜잭션을 시작
 * 핵심 로직 실행
 * 핵심 로직 실행에 문제가 없으면 커밋
 * 핵심 로직 실행에 예외가 발생하면 롤백
 * */
@Slf4j
@Aspect
public class AspectV3 {
    //hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder(){}

    /**
     * 클래스 이름 패턴이 *Service, XxxService 처럼 Service 로 끝나는 것을 대상으로 한다.
     * Servi* 과 같은 패턴도 가능
     * 여기서 타입 이름 패턴이라고 한 이유는 클래스, 인터페이스에 모두 적용되기 때문
     * 반환 타입, 메서드 이름, 파라미터가 뭔지는 상관없는
     *
     * OrderService 는 빈으로 등록안되니 제외, 빈으로 등록되는 OrderServiceImpl 을 검사해
     * 조건에 맞으면 OrderServiceImple 대신 프록시 객체를 빈으로
     * */
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService(){}

    //AspectV1 와 같음, 어드바이스 적용되는 클래스 메서드 출력
    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        //어떤 패키지에서 어떤 클래스의 메서드가 실행되었는지
        log.info("[log] {}", joinPoint.getSignature());

        /** joinPoint.proceed() 안 써주면 핵심 기능 호출 안함, 다음 부가 기능 호출 안함*/
        return joinPoint.proceed();
    }

    /**
     * 포인트컷은 이렇게 조합할 수 있다. && (AND), || (OR), ! (NOT) 3가지 조합이 가능하다.
     * hello.aop.order 패키지와 하위 패키지 이면서 타입 이름 패턴이 *Service 인 것을 대상으로 한다.
     * 결과적으로 doTransaction() 어드바이스는 OrderService 에만 적용된다.
     * doLog() 어드바이스는 OrderService , OrderRepository 에 모두 적용된
     */
    @Around("allOrder() && allService()")
//    @Around("execution(* hello.aop.order..*(..)) && execution(* *..*Service.*(..))")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());

            //프로그램 동작, 핵심 로직
            Object result = joinPoint.proceed();

            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());

            return result;

        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());

            throw e;

        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
