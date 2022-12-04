package hello.aop.internalcall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 앞선 방법들은 자기 자신을 주입하거나 또는 Provider 를 사용해야 하는 것 처럼 조금 어색한 모습을 만들었다.
 * 가장 나은 대안은 내부 호출이 발생하지 않도록 구조를 변경하는 것이다. 실제 이 방법을 가장 권장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {

    /**
     * 어드바이저는 CallLogAspect 클래스
     * InternalService도 어드바이저의 포인트컷 범위에 부합하므로
     * 프록시 객체가 주입됨
     * @Component 붙어있으니 빈으로 자동 등록 되는데 포인트컷 범위에 부합하므로
     * 프록시 객체 빈으로 등록됨
     */

    //내부 호출하는 메서드 다른 클래스에 따로 넣어
    private final InternalService internalService;

    public void external() {
        log.info("call external");

        /**따로 생성해 분리 내부 호출하는걸, 내부 호출 자체가 사라짐
         * 외부 메서드 호출*/
        internalService.internal();
    }
}
/**
 * AOP는 주로 트랜잭션 적용이나 주요 컴포넌트의 로그 출력 기능에 사용된다. 쉽게 이야기해서
 * 인터페이스에 메서드가 나올 정도의 규모에 AOP를 적용하는 것이 적당하다. 더 풀어서 이야기하면 AOP는
 * public 메서드에만 적용한다. private 메서드처럼 작은 단위에는 AOP를 적용하지 않는다.
 * AOP 적용을 위해 private 메서드를 외부 클래스로 변경하고 public 으로 변경하는 일은 거의 없다.
 * 그러나 위 예제와 같이 public 메서드에서 public 메서드를 내부 호출하는 경우에는 문제가 발생한다.
 * 실무에서 꼭 한번은 만나는 문제이기에 이번 강의에서 다루었다.
 * AOP가 잘 적용되지 않으면 내부 호출을 의심해보자.
 */