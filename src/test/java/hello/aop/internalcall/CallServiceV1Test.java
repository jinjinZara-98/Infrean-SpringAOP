package hello.aop.internalcall;

import hello.aop.exam.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 스프링 부트 2.6부터는 순환 참조를 기본적으로 금지하도록 정책이 변경되었다.
 * 따라서 이번 예제를 스프링 부트 2.6 이상의 버전에서 실행하면 다음과 같은 오류 메시지가 나오면서 정상 실행되지 않는다.
 * Error creating bean with name 'callServiceV1': Requested bean is currently in
 * creation: Is there an unresolvable circular reference?
 *
 * 이 문제를 해결하려면 application.properties 에 다음을 추가해야 한다.
 * spring.main.allow-circular-references=true
 */
@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV1Test {

    @Autowired
    CallServiceV1 callServiceV1;

    //external()안에서 internal() 실행되도 AOP 적용됨
    //callServiceV1.internal()에서 callServiceV1은 프록시 객체가 주입됬으니
    @Test
    void external() {
        callServiceV1.external();
    }
}
