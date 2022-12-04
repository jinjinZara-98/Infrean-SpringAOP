package hello.aop.exam;

import hello.aop.exam.aop.RetryAspect;
import hello.aop.exam.aop.TraceAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
//@Import(TraceAspect.class)
//@Aspect 붙은 클래스 빈으로 등록, 어드바이저로 등록됨
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest
public class ExamTest {

    @Autowired
    ExamService examService;

    //@Trace 가 붙은 request() , save() 호출시 로그가 잘 남는 것을 확인
    @Test
    void test() {
        for (int i = 0; i < 5; i++) {
            //맨 처음 이거부터 출력
            log.info("client request i={}", i);

            examService.request("data " + i);
        }
    }
}
