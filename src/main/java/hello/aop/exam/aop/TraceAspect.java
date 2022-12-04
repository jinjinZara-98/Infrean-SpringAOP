package hello.aop.exam.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/** 어드바이저 생성,*/
@Slf4j
@Aspect
public class TraceAspect {

    /**
     * @Trace가 있는 메서드에는 이 어드바이스가 적용되는
     * 애노테이션을 씀, @Trace의 경로를 같이 적어줌
     *
     * 서비스 리포지토리 모두 있으니 모두 적용
     *
     * @Before는 joinPoint.proceed() 안해줘도 됨
     *
     * @Before는 joinPoint이전에 실행
     **/
    @Before("@annotation(hello.aop.exam.annotation.Trace)")
    public void doTrace(JoinPoint joinPoint) {

        //인수정보 남기기
        Object[] args = joinPoint.getArgs();

        //메서드 시그니처와 메서드 파라미터로 들어온 값 로그로 출력
        log.info("[trace] {} args={}", joinPoint.getSignature(), args);
    }
}
