package hello.aop.exam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @Retry 애노테이션이 있으면 예외가 발생했을 때 다시 시도해서 문제를 복구
 *
 * @Target(ElementType.METHOD) 메서드에 붙일 수 있는
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    //이 애노테이션에는 재시도 횟수로 사용할 값이 있다. 기본값으로 3
    int value() default 3;
}
