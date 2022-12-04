package hello.aop.proxyvs.code;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

//어드바이저
@Slf4j
@Aspect
public class ProxyDIAspect {

    //반환타입 메서드이름 파라미터 상관없고
    //aop패키지와 그 하위 패키지에서 아무 타입이나 상관없는
    @Before("execution(* hello.aop..*.*(..))")
    public void doTrace(JoinPoint joinPoint) {
        log.info("[proxyDIAdvice] {}", joinPoint.getSignature());
    }
}
