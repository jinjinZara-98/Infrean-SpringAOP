package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 이것도 internalcall패키지 안에 있으니 CallLogAspect 포인트컷 조건에 부합
 * 적용되는 어드바이저는 CallLogAspect
 * */
@Slf4j
@Component
public class InternalService {

    public void internal() {
        log.info("call internal");
    }
}
