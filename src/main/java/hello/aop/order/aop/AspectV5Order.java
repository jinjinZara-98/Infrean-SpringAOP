package hello.aop.order.aop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 쌉 중요 씨발라마
 *
 * 순서를 지정하고 싶으면 @Aspect 적용 단위로 @Order 애노테이션을 적용해야 한다.
 * 문제는 이것을 어드바이스 단위가 아니라 클래스 단위로 적용할 수 있다는 점이다.
 * 그래서 지금처럼 하나의 애스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다.
 * 따라서 애스펙트를 별도의 클래스로 분리해야
 *
 * 하나의 애스펙트 안에 있던 어드바이스를 LogAspect , TxAspect 애스펙트로 각각 분리해야
 * 정적 클래스로 만들어줌
 *
 * AspectV3 와 어드바이스 같고 AspectV4 처럼 Pointout 에서 포인트컷 가져옴
 * */
@Slf4j
public class AspectV5Order {

    //클래스를 내부에 만들어 @Aspect 붙여주고 @Order로 순서 지정
    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

    /** 트랜잭션 로그를 먼저 남기도록 순서 지정*/
    @Aspect
    @Order(1)
    public static class TxAspect {

        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
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
}
