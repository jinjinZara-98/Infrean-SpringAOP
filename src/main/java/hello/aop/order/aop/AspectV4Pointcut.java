package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 포인트컷을 하나의 외부 클래스를 만들어 보관해 사용하는
 * v3에서 외부 포인트컷을 사용하는 것
 *
 * 에스펙트 단위로 순서가 보장
 * */
@Slf4j
@Aspect
public class AspectV4Pointcut {

    /**
     * 사용하는 방법은 패키지명을 포함한 클래스 이름과 포인트컷 시그니처를 모두 지정
     * 포인트컷을 여러 어드바이스에서 함께 사용할 때 이 방법을 사용하면 효과적
     *
     * 포인트컷 모아둔 Pointcuts 클래스의 포인트컷 메서드 이름 allOrder 지정
     * 해당 포인트컷 범위를 사용
     * */
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());

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
