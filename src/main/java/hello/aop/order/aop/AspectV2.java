package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Pointcut 사용
 * @Pointcut 에 포인트컷 표현식을 사용한다.
*/
@Slf4j
@Aspect
public class AspectV2 {

    /**
     * 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니처(signature)
     * 메서드의 반환 타입은 void 여야 한다.
     * 코드 내용은 비워둔다
     * private , public 같은 접근 제어자는 내부에서만 사용하면 private 을 사용해도 되지만,
     * 다른 애스팩트에서 참고하려면 public 을 사용해야
     * 여기서 내부는 포인트컷이 있는 클래스 안
     *
     * 결과적으로 AspectV1 과 같은 기능을 수행한다.
     * 이렇게 분리하면 하나의 포인트컷 표현식을 여러 어드바이스에서 함께 사용할 수 있다.
     * 다른 클래스에 있는 외부 어드바이스에서도 포인트컷을 함께 사용할 수 있다
     * */
    @Pointcut("execution(* hello.aop.order..*(..))") //pointcut expression
    private void allOrder(){} //pointcut signature

    /**
     * 입력한 값과 동일한 이름을 갖는 메서드가 포인트컷 범위 지정
     * 적용 범위를 지정하는 포인트컷을 메서드로 따로 빼내에 메서드 이름을 적어주어 포인트컷 적용
     * */
    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());

        return joinPoint.proceed();
    }
}
