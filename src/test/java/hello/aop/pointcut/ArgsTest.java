package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 메서드 파라미터 타입으로만 포인트컷 조건?
 *
 * args : 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
 * 기본 문법은 execution 의 args 부분과 같다.
 *
 * execution과 args의 차이점
 *
 * execution 은 파라미터 타입이 정확하게 매칭되어야 한다.
 * execution 은 클래스에 선언된 정보를 기반으로 판단한다.
 *
 * args 는 부모 타입을 허용한다. args 는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단
 * */
public class ArgsTest {

    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {

        /**
         * 메서드 미리 지정
         * 파라미터 이름이 name 이고 타입이 String
         */
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }


    /**
     * 포인트컷 하나만 만들어서 뻇다
     * 테스트를 편리하게 진행하기 위해 포인트컷을 여러번 지정하기 위해 포인트컷 자체를 생성하는 메서드
     * 파라미터로 포인트컷 범위 받음
     */
    private AspectJExpressionPointcut pointcut(String expression) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        return pointcut;
    }

    @Test
    void args() {

        /**
         * 여기서 포인트컷 파라미터로 넣어 만드는
         * hello(String)과 매칭
         * helloMethod는 파라미터가 String이니 매칭 됨
         * */
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        /** Object라고 해동 무방, 클래스에 선언된 정보, 부모 타입 넣어도 가능 */
        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        //타입이 아무것도 없으니 false로
        assertThat(pointcut("args()")
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();

        assertThat(pointcut("args(..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        assertThat(pointcut("args(*)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        assertThat(pointcut("args(String,..)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * execution(* *(java.io.Serializable)): 메서드의 시그니처로 판단 (정적)
     * args(java.io.Serializable): 런타임에 전달된 인수로 판단 (동적) 객체 인스턴스
     */
    @Test
    void argsVsExecution() {
        //Args
        assertThat(pointcut("args(String)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        //String클래스로 들어가보면 Serializable)란걸 구현하고 있음
        //결국 이게 부모 클래스임
        assertThat(pointcut("ags(java.io.Serializable)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        assertThat(pointcut("args(Object)")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        //Execution은 정확하게 매칭이 되지 않음, 정적인 정보만, 즉 정확하게 매칭되어야함
        assertThat(pointcut("execution(* *(String))")
                .matches(helloMethod, MemberServiceImpl.class)).isTrue();

        assertThat(pointcut("execution(* *(java.io.Serializable))") //매칭 실패
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();

        assertThat(pointcut("execution(* *(Object))") //매칭 실패
                .matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
}
