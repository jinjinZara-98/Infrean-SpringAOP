package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 다음은 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
 * this, target, args,@target, @within, @annotation, @args
 * 포인트컷의 이름과 매개변수의 이름을 맞추어야 한다. 여기서는 arg 로 맞추었다.
 * 추가로 타입이 메서드에 지정한 타입으로 제한
 *
 * @target , @within : 타입의 애노테이션을 전달 받는다.
 * @annotation : 메서드의 애노테이션을 전달 받는다. 여기서는 annotation.value() 로 해당
 * 애노테이션의 값을 출력하는 모습을 확인할 수 있다
 * */
@Slf4j
//밑에 어드바이스인 @Aspect 붙은 클래스를 빈으로
@Import(ParameterTest.ParameterAspect.class)
@SpringBootTest
public class ParameterTest {

    @Autowired
    MemberService memberService;

    @Test
    void success() {

        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ParameterAspect {

        //member패키지의 모든 것에 걸겠다는
        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() {
        }

        //logArgs1 : joinPoint.getArgs()[0] 와 같이 매개변수를 전달 받는다.
        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            //hello A값을 얻어내는
            Object arg1 = joinPoint.getArgs()[0];
            log.info("[logArgs1]{}, arg={}", joinPoint.getSignature(), arg1);

            return joinPoint.proceed();
        }

        /**
         * logArgs2 : args(arg,..) 와 같이 매개변수를 전달 받는다.
         * args를 쓰면 arg를 Object arg로 받을 수 있음, Object arg1 = joinPoint.getArgs()[0] 없어도 됨
         * Object가 아닌 String도 쓸 수 있지만 더 많이 받을 수 있게
         *
         * args(arg,..)는 args(Object,..) 라고 봐도 무방, 메서드에서 지정한 타입으로 제한하니
         * */
        @Around("allMember() && args(arg,..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {

            log.info("[logArgs2]{}, arg={}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        }


        /**
         * logArgs3 : @Before 를 사용한 축약 버전이다. 추가로 타입을 String 으로 제한했다.
         * 메서드 파라미터 String arg
         * 더 간단하게
         */
        @Before("allMember() && args(arg,..)")
        public void logArgs3(String arg) {
            log.info("[logArgs3] arg={}", arg);
        }

        /**
         * this : 프록시 객체를 전달 받는다.
         * this는 객체 인스턴스의 Object가 넘어옴
         * target도 클래스를 직접 지정
         * target은 실제 대상 구현체, this는 컨테이너에 올라간 애
         *
         * 현재 CGLIB 로 프록시 생성함, 인터페이스가 있어도 구현 클래스를 가지고 프록시를 생성
         * this: 스프링 컨테이너에 등록된 프록시 빈과 매칭, 부모도 허용
         * target: 제 target 객체를 대상으로 포인트컷 매칭, 부모도 허용
         * */
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * target : 실제 대상 객체를 전달 받는다. 프록시가 호출하는 실제 대상
         * */
        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        /**
         * 애노테이션 정보를 받아오는
         * @target 실행 객체 클래스에 @ClassAop가 붙어있는지, 그 클래스의 부모 클래스 메서드까지 호출
         * */
        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation) {

            //@ClassAop가 붙어있는 클래스의 메서드 시그니처와 @ClassAop의 어노테이션 정보
            log.info("[@target]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        /**
         * 이것도 애노테이션 정보 가져오는
         * @within은 실행 객체 클래스에 @ClassAop가 붙어있는지, 부모까지는 X
         * */
        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {

            //@ClassAop가 붙어있는 클래스의 메서드 시그니처와 @ClassAop의 어노테이션 정보
            log.info("[@within]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        /**
         * annotation.value()로 애노테이션에 들어있는 값을 꺼낼 수 있음,
         * @MethodAop("test value")
         *
         * 메서드에 @MethodAop가 붙어있는  메서드 시그니처와  @MethodAop 어노테이션의 값
         * */
        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            log.info("[@annotation]{}, annotationValue={}", joinPoint.getSignature(), annotation.value());
        }
    }
}