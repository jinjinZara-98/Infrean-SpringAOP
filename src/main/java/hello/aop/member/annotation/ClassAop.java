package hello.aop.member.annotation;

import java.lang.annotation.*;

/**
 * aop 포인트컷에 사용하는 어노테이션, 이 어노테이션이 붙여진 클래스는 공통로직 적용하게 하는
 * MemberServiceImpl에 적용되는
 *
 * @interface는 aop를 만들기 위해
 * @Target(ElementType.TYPE)는 클래스에 붙이는 애노테이션
 * @Retention(RetentionPolicy.RUNTIME) 실제 실행할때까지 애노테이션이 살아있는
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassAop {
}
