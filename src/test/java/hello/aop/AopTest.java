package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
/**
 * @Aspect 는 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다.
 *따라서 AspectV1 를 AOP로 사용하려면 스프링 빈으로 등록해야
 *
 * order/aop 폴더의 공통로직 테스트
 * */
//@Import(AspectV1.class)
//@Import(AspectV2.class)
@Import(AspectV3.class)
//@Import(AspectV4Pointcut.class)
//@Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
//@Import(AspectV6Advice.class)
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void aopInfo() {
        /**
         * AopUtils.isAopProxy(...) 을 통해서 AOP 프록시가 적용 되었는지 확인할 수 있다
         * AOP 프록시가 적용 되었다면 true
         *
         * 현재는 AOP 적용하지 않았으니 false
         * */
        log.info("isAopProxy, orderService={}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, orderRepository={}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    //orderItem() 파라미터에 ex 넣으면 해당 예외 터지는지
    @Test
    void exception() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
