package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * //프록시와 내부 호출 - 대안1 자기 자신 주입
 * 메서드 내부 호출은 프록시 적용 못함
 *
 * 참고: 생성자 주입은 순환 사이클을 만들기 때문에 실패한다.
 */

@Slf4j
@Component
public class CallServiceV1 {

    private CallServiceV1 callServiceV1;

    /**
     * 그냥 생성자 의존주입은 불가, 자기 자신이 생성도 안된 상태에서 주입되는건 말이 안됌
     *
     * 스프링에서 AOP가 적용된 대상을 의존관계 주입 받으면 주입 받은 대상은 실제 자신이 아니라 프록시 객체이다.
     *
     * CallServiceV0는 테스트에서 CallServiceV0를 주입받지
     * 해당 클래스에서 자기 자신 객체를 주입받지 않음
     *
     * external() 을 호출하면 callServiceV1.internal() 를 호출하게 된다.
     * 주입받은 callServiceV1 은 프록시이다. 따라서 프록시를 통해서 AOP를 적용할 수 있다
     * */
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        //프록시 통해서 호출, 나갔다 들어온다
        //외부 메서드 호출
        //CallServiceV0는 그냥 internal()이였음
        callServiceV1.internal();
    }

    public void internal() {
        log.info("call internal");
    }
}

