package hello.aop.exam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 먼저 로그 출력용 AOP를 만들어보자.
 * @Trace 가 메서드에 붙어 있으면 호출 정보가 출력되는 편리한 기능이
 *  */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {
}
