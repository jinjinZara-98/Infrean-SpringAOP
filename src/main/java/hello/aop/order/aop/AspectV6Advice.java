package hello.aop.order.aop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**어드바이스 종류
 *
 * @Around : 메서드 호출 전 후에 수행, 가장 강력한 어드바이스,
 * 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
 *
 * @Around의 큰 기능을 조각조각 내서 조금 실행하게
 * @Before : 조인 포인트 실행 이전에 실행
 * @AfterReturning : 조인 포인트가 정상 완료후 실행
 * @AfterThrowing : 메서드가 예외를 던지는 경우 실행
 * @After : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)
 *
 * 복잡해 보이지만 사실 @Around 를 제외한 나머지 어드바이스들은
 * @Around 가 할 수 있는 일의 일부만 제공할 뿐이다.
 * 따라서 @Around 어드바이스만 사용해도 필요한 기능을 모두 수행
 *
 * JoinPoint 인터페이스의 주요 기능
 *
 * getArgs() : 메서드 인수를 반환합니다.
 * getThis() : 프록시 객체를 반환합니다.
 * getTarget() : 대상 객체를 반환합니다.
 * getSignature() : 조언되는 메서드에 대한 설명을 반환합니다.
 * toString() : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.
 *
 * ProceedingJoinPoint 인터페이스의 주요 기능
 * proceed() : 다음 어드바이스나 타켓을 호출한다
 * */
@Slf4j
@Aspect
public class AspectV6Advice {

    /**
     * @Around
     * 모든 어드바이스는 org.aspectj.lang.JoinPoint 를 첫번째 파라미터에 사용할 수 있다.
     * (생략해도 된다.)
     * 단 @Around 는 ProceedingJoinPoint 을 사용
     *
     * 메서드의 실행의 주변에서 실행된다. 메서드 실행 전후에 작업을 수행한다.
     * 가장 강력한 어드바이스
     * 조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택
     * 전달 값 변환: joinPoint.proceed(args[])
     * 반환 값 변환
     * 예외 변환
     * 트랜잭션 처럼 try ~ catch~ finally 모두 들어가는 구문 처리 가능
     * 어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint 를 사용해야 한다.
     * proceed() 를 통해 대상을 실행한다.
     * proceed() 를 여러번 실행할 수도 있음(재시도)
     *
     * @Around 는 항상 joinPoint.proceed() 를 호출해야
     * */

    //포인트컷 클래스 Pointcouts 경로와 포인트컷 메서드 이름 적어준
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try {
            //@Before
            log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();

            //@AfterReturning
            log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());

            return result;
        } catch (Exception e) {

            //@AfterThrowing
            log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {

            //@After
            log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    /**
     * 실행 순서: @Around , @Before , @After , @AfterReturning , @AfterThrowing
     * 어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대
     * 여기서 실행 순서란 어드바이저 만들때 사용하는 어노테이션의 실행 순서
     *
     * 물론 @Aspect 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다.
     * 그래서 AspectV3 AspectV4 순서 보장되지 않는
     *
     * 이 경우 앞서 배운 것처럼 @Aspect 를 분리하고 @Order 를 적용
     * */

    /**
     * ProceedingJoinPoint는 @Around에서만 사용 가능, 나머지는 JoinPoint
     * 다음 로직 실행 안함, 할 필요 없음
     *
     * 조인포인트 실행하기전에만 남기고 싶다
     * @Around 와 다르게 작업 흐름을 변경할 수는 없다.
     * @Around 는 ProceedingJoinPoint.proceed() 를 호출해야 다음 대상이 호출된다.
     * 만약 호출하지 않으면 다음 대상이 호출되지 않는다.
     *
     * 반면에 @Before 는 ProceedingJoinPoint.proceed() 자체를 사용하지 않는다.
     * 어차피 @Before 로 하면 핵심로직 실행 전 호출한다는걸로 어드바이스 실행 시점을 정한거니까
     * 메서드 종료시 자동으로 다음 타켓이 호출된다. 물론 예외가 발생하면 다음 코드가 호출되지는 않는다.
     */
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    /**
     * 메서드 실행이 정상적으로 반환될 때 실행
     * returning = "result" 리턴되는 값의 이름 doTransaction() 반환값 이름이 이거임
     * Object result와 매칭되 returning = "result" 값이 여기로 들어옴
     * @Around에서는 리턴값 조작가능하지만 여기선 불가
     * returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
     * returning 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다.
     *  (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
     * @Around 와 다르게 반환되는 객체를 변경할 수는 없다. 반환 객체를 변경하려면 @Around 를 사용해야 한다.
     * 참고로 반환 객체를 조작할 수 는 있다
     *
     * @Aroumd 다음 순서이니 @AfterReturning
     * @Aroumd 어드바이스 호출 후 거기서 생성하고 반환하는 객체 result 사용할 수 있는
     * joinPoint.proceed() 로 다음에 적용되는 어드바이스 호출하니
     */
    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
            returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    /**
     * 메서드 실행이 예외를 던져서 종료될 때 실행
     *
     * throwing = "ex"와 Exception ex 맞아야함
     * @Around는 throw를 해줘야하지만 여기서는 자동으로 됨
     * throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
     * throwing 절에 지정된 타입과 맞은 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
     * */
    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()",
            throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
    }

    /**
     * 메서드 실행이 종료되면 실행된다. (finally를 생각하면 된다.)
     * 정상 및 예외 반환 조건을 모두 처리한다.
     * 일반적으로 리소스를 해제하는 데 사용한다
     * */
    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
