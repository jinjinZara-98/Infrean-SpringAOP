package hello.aop.exam.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/** 하나의 어드바이저 */
@Slf4j
@Aspect
public class CallLogAspect {

    /**
     * ..* 는 하위 패키지의 어떤 클래스든
     *
     * 반환타입 패키지명 메서드명 파라미터
     * */
    @Before("execution(* hello.aop.internalcall..*.*(..))")
    public void doLog(JoinPoint joinPoint) {

        log.info("aop = {}", joinPoint.getSignature());
    }
}
