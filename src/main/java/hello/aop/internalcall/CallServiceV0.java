package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CallLogAspect 어드바이저 사용하는
 *
 * @within @args 테스트?
 *
 * 스프링은 프록시 방식의 AOP를 사용한다.
 * 따라서 AOP를 적용하려면 항상 프록시를 통해서 대상 객체(Target)을 호출해야 한다.
 * 이렇게 해야 프록시에서 먼저 어드바이스를 호출하고, 이후에 대상 객체를 호출한다.
 * 만약 프록시를 거치지 않고 대상 객체를 직접 호출하게 되면 AOP가 적용되지 않고,
 * 어드바이스도 호출되지 않는다.
 * AOP를 적용하면 스프링은 대상 객체 대신에 프록시를 스프링 빈으로 등록한다.
 * 따라서 스프링은 의존관계 주입시에 항상 프록시 객체를 주입한다.
 * 프록시 객체가 주입되기 때문에 대상 객체를 직접 호출하는 문제는 일반적으로 발생하지 않는다.
 *
 * 하지만 대상 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생한다.
 * 실무에서 반드시 한번은 만나서 고생하는 문제
 * */

@Slf4j
@Component
public class CallServiceV0 {
    //외부에서 발생한다는
    public void external() {
        log.info("call external");
        /**자바 언어에서 메서드를 호출할 때 대상을 지정하지 않으면 앞에 자기 자신의 인스턴스를 뜻하는 this 가 붙게 된다.
        //그러니까 여기서는 this.internal() 이라고 이해, 내부 메서드 호출(this.internal())*/
        internal();
    }

    //내부
    public void internal() {
        log.info("call internal");
    }
}
