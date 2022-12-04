package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * MethodAop 이용
 *
 * @annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
 *
 * @target @within은 실행 객체 클래스
 * */
@Slf4j
//밑에 셍성한 어드바이저 등록
@Import(AtAnnotationTest.AtAnnotationAspect.class)
@SpringBootTest
public class AtAnnotationTest {

    //MemberServiceImpl 이 주입됨
    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());

        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class AtAnnotationAspect {

        /**
         * MethodAop 걸린거에 어드바이스 적용시키겠다, MemberServiceImpl에 MethodAop가 있으니
         *
         * hello.aop.member 패키지에 @MethodAop가 걸린 메서드
         *
         * MemberServiceImpl클래스 hello메서드에 걸림
         *
         * 어노테이션 패키지 경로부터 다 적어줘야야         * */
        @Around("@annotation(hello.aop.member.annotation.MethodAop)")
        public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

            log.info("[@annotation] {}", joinPoint.getSignature());

            return joinPoint.proceed();
        }

    }
}
/** @args
 *
 * 정의
 * @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
 *
 * 설명
 * 전달된 인수의 런타임 타입에 @Check 애노테이션이 있는 경우에 매칭한다.
 * @args(test.Check)
 *
 * 많이 사용하지 않음
 * */