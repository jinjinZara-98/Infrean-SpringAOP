package hello.aop.exam;

import hello.aop.exam.annotation.Retry;
import hello.aop.exam.annotation.Trace;
import org.springframework.stereotype.Repository;

/**
 * 실전 예제 만들기
 *
 * @Trace 애노테이션으로 로그 출력하기
 * @Retry 애노테이션으로 예외 발생시 재시도 하기
 */
@Repository
public class ExamRepository {

    //어쩌다 한 번 실패하므로
    private static int seq = 0;

    /**
     * 5번에 1번 실패하는 요청
     *
     * @Retry는 횟수제한이 있어야함, 무제한으로 셀프디도스 만들 수 있음
     * 기본값 변경, 원래 3이였음
     *
     * 이 메서드에서 문제가 발생하면 4번 재시도
     */
    @Trace
    @Retry(value = 4)
    public String save(String itemId) {
        seq++;

        //5로 나누어 떨어지면 예외 발생
        if (seq % 5 == 0) {
            throw new IllegalStateException("예외 발생");
        }

        return "ok";
    }
}
