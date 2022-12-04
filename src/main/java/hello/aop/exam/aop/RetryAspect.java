package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/** 어드바이저 생성*/
@Slf4j
@Aspect
public class RetryAspect {

    /**
     * 얘는 @Around 써야함, 재시도 할 때 내가 언제 조인포인트에 프로시드를 호출할지 결정해야 하기 때문
     * Retry retry를 받으므로 "@annotation(retry)
     *
     * @annotation 메서드에 주어진 어노테이션 갖고있는지
     * ExamRepository만 갖고있음
     */
    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {

        //포인트컷에 걸린 메서드 시그니처, @Retry 어노테이션 정보 출력
        log.info("[retry] {} retry={}", joinPoint.getSignature(), retry);

        /** 어노테이션의 값을 꺼냄 */
        int maxRetry = retry.value();

        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {

            try {
                //몇번 재시도 했는지, @Retry 어노테이션의 값 최대 재시도 값 출력
                log.info("[retry] try count={}/{}", retryCount, maxRetry);

                return joinPoint.proceed();
            } catch (Exception e) {

                /** 예외가 터지면 터진 예외 담아두기 */
                exceptionHolder = e;
            }
        }

        //리턴 횟수가 넘어가면 예외 던지기, 마지막에 터진 예외
        throw exceptionHolder;
    }
}
