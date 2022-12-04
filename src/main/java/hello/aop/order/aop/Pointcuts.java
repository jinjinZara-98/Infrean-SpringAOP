package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * 포인트컷 따로 모아둔 클래스, 외부에서도 불러 쓸 수 있게 public, 공용으로 쓰는
 * */
public class Pointcuts {

    //hello.springaop.app 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder(){}

    //타입 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService(){}

    //allOrder && allService
    @Pointcut("allOrder() && allService()")
    public void orderAndService(){}
}
