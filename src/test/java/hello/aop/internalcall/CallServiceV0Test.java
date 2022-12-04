package hello.aop.internalcall;

import hello.aop.exam.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * CallServiceV0가 @Component가 붙어있어 컴포넌트스캔 대상이 됨
 * CallLogAspect에 걸림, AOP와 프록시가 다 먹힘
 * */
@Slf4j
/**어드바이저 빈으로 등록, 이렇게 해서 CallServiceV0 에 AOP 프록시를 적용 */
@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV0Test {

    /**
     * 여기에 주입되는건 프록시
     * 빈으로 등록되려는거 위에 등록한 어드바이저의 포인트컷 조건으로
     * 어드바이스 적용 여부 프록시 객체 생성 여부 판단
     * 프록시 객체 생성해야되면 원래 등록하려던 빈 대신 프록시 객체 빈으로 등록
     * */
    @Autowired
    CallServiceV0 callServiceV0;

    /**
     * 이 테스트 실행시 internal은 AOP적용이 안됨
     * this.internal() 하기 때문에 자기 객체 자신의 메서드 호출하므로
     * */
    @Test
    void external() {
        callServiceV0.external();
    }

    @Test
    void internal() {
        callServiceV0.internal();
    }
}
/**
 * 결과
 * external()을 호출하면서 어드바이스가 적용되고 external()안의
 * internal()를 호출할 때는 적용 안 됨, 결과적으로 자기 자신의 내부 메서드를 호출하는 this.internal()
 * 하지만 그냥 internal()는 적용 됨, 외부에서 호출하는 경우이니
 */
