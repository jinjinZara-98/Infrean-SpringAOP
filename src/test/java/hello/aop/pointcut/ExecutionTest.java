package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

/**
 * 지금부터 포인트컷 표현식을 포함한 포인트컷에 대해서 자세히 알아보자.
 *
 * 애스펙트J 는 포인트컷을 편리하게 표현하기 위한 특별한 표현식을 제공한다.
 * 예) @Pointcut("execution(* hello.aop.order..*(..))")
 * 포인트컷 표현식은 AspectJ pointcut expression
 * 즉 애스펙트J가 제공하는 포인트컷 표현식을 줄여서 말하는 것이다.
 *
 * 포인트컷 지시자
 * 포인트컷 표현식은 execution 같은 포인트컷 지시자(Pointcut Designator)로 시작한다.
 * 줄여서 PCD라 한다.
 *
 * 포인트컷 지시자의 종류
 * execution : 메소드 실행 조인 포인트를 매칭한다.
 * 스프링 AOP에서 가장 많이 사용하고, 기능도 복잡하다.
 * within : 특정 타입 내의 조인 포인트를 매칭한다.
 * args : 인자가 주어진 타입의 인스턴스인 조인 포인트
 * this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
 * target : Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
 * @target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
 * @within : 주어진 애노테이션이 있는 타입 내 조인 포인트
 * @annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
 * @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
 * bean : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다
 * */
@Slf4j
public class ExecutionTest {

    /**
     * pointcut에 포인트컷 문법을 넣을 수 있는
     * AspectJExpressionPointcut 이 바로 포인트컷 표현식을 처리해주는 클래스다.
     * 여기에 포인트컷 표현식을 지정하면 된다.
     * AspectJExpressionPointcut 는 상위에 Pointcut 인터페이스를 가진다
     *
     * helloMethod 는 메서드 정보 담는
     * */
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    /**
     * 테스트 메서드 실행 전 실행
     * */
    @BeforeEach
    public void init() throws NoSuchMethodException {
        /**
         * 리플렉션으로 메서드 정보 뽑는, 파라미터 타입이 스트링인거 찾는
         * 여러 테스트에서 쓸 수 있게, 테스트가 실행될 떄마다 값을 뽑아 여기에 넣어두는
         * MemberServiceImpl의 hello메서드를 대상으로 포인트컷의 유무 확인, String은 반환타입
         * */
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    /** MemberServiceImpl.hello(String) 메서드의 정보를 출력 */
    @Test
    void printMethod() {

        /**
         * 포인트컷 적용 지점, 메서드 정보 출력
         * public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
         */
        log.info("helloMethod={}", helloMethod);
    }

    /**
     * execution 문법
     *
     * execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
     * 메소드 실행 조인 포인트를 매칭한다.
     * ?는 생략할 수 있다.
     * * 같은 패턴을 지정할 수 있다.
     *
     * MemberServiceImpl의 hello메서드와 가장 정확하게 매칭
     * AspectJExpressionPointcut 에 pointcut.setExpression 을 통해서 포인트컷 표현식을 적용할 수 있다.
     *
     * 매칭 조건
     * 접근제어자?: public
     * 반환타입: String
     * 선언타입?: hello.aop.member.MemberServiceImpl
     * 메서드이름: hello
     * 파라미터: (String)
     * 예외?: 생략
     * */
    @Test
    void exactMatch() {
        //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");

        /**
         * pointcut.matches(메서드, 대상 클래스) 를 실행하면 지정한 포인트컷 표현식의 매칭 여부를 true , false 로 반환한다.
         * 대상클래스라는건 해당 메서드가 있는 클래스를 말하는건가
         * */
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 가장 많이 생략한 포인트컷
     *
     * 매칭 조건
     * 접근제어자?: 생략
     * 반환타입: *
     * 선언타입?: 생략, 패키지 말하는거
     * 메서드이름: *
     * 파라미터: (..)
     * 예외?: 없음
     * * 은 아무 값이 들어와도 된다는 뜻이다.
     * 파라미터에서 .. 은 파라미터의 타입과 파라미터 수가 상관없다는 뜻
     * */
    @Test
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //메서드 이름 매칭 관련 포인트컷
    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //패턴 매칭
    @Test
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //앞 뒤가 다 되는
    @Test
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //매칭이 실패하는 케이스
    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /** 메서드 이름 앞 뒤에 * 을 사용해서 매칭할 수 있다 */

    //패키지 매칭 관련 포인트컷, hello 메서드이름
    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //타입과 메서드 이름에 별 넣기, member 패키지에 어떤 클래스든 상관없고, 어떤 메서드인지도 상관없다
    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //aop패키지만,
    @Test
    void packageExactFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //aop패키지와 그 하위 패키지는 aop..
    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * hello.aop.member.*(1).*(2)
     *
     * (1): 타입
     * (2): 메서드 이름
     * 패키지에서 . , .. 의 차이를 이해해야 한다.
     * . : 정확하게 해당 위치의 패키지
     * .. : 해당 위치의 패키지와 그 하위 패키지도 포함
     *
     * 타입 매칭 - 부모 타입 허용, 타입 정보가 정확하게 일치하기 때문에 매칭
     * hello Method는 MemberServiceImpl타입 안에 있기 때문에
     * */
    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /** MemberServiceImpl이 아닌 인터페이스를 넣음 */
    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /** MemberService는 hello만 있지만 MemberServiceImpl은 internal이라는 메서드가 더 있는데 매칭이 되는지 */
    @Test
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        //지정한 포인트컷 표현식의 매칭 여부
        //포인트컷으로 설정한 조건에 internalMethod 메서드가 있느냐
        //internalMethod 메서드가 MemberServiceImpl.class에 있는지
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        /** 이건 인터페이스로 지정 */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");

        /**
         * 자식타입에 있는 다른메서드까지 매칭이 되느냐, 불가
         *
         * 부모에서 선언한 메서드까지만 가능, emberService 에는 internal(String) 메서드가 없으니
         * 부모 타입을 표현식에 선언한 경우 부모 타입에서 선언한 메서드가 자식 타입에 있어야 매칭에 성공한다.
         * 그래서 부모 타입에 있는 hello(String) 메서드는 매칭에 성공하지만,
         * 부모 타입에 없는 internal(String) 는 매칭에 실패
         * */
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    /** 파라미터 매칭 */

    /**
     * String 타입의 파라미터 허용
     *
     * (String)
     * 반환값 String이니 참
     * */
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 파라미터가 없어야 함
     *
     * ()
     * String이 존재하므로 false.fh
     * */
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 정확히 하나의 파라미터 허용, 모든 타입 허용
     * (Xxx)
     * */
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 숫자와 무관하게 모든 파라미터, 모든 타입 허용
     * (), (Xxx), (Xxx, Xxx)
     * */
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
     *
     * (String), (String, Xxx), (String, Xxx, Xxx)
     * (String, *) 파라미터 개수가 2갠데 두번째 파라미터는 아무거나
     * (String, ..)는 없어도 되고 무제한이어도 되는
     * */
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * execution 파라미터 매칭 규칙은 다음과 같다.
     *
     * (String) : 정확하게 String 타입 파라미터
     * () : 파라미터가 없어야 한다.
     * (*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
     * (*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
     * (..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다.
     * 0..* 로 이해하면 된다.
     * (String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
     * 예) (String) , (String, Xxx) , (String, Xxx, Xxx) 허용
     * */
}
