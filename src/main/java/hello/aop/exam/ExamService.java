package hello.aop.exam;

import hello.aop.exam.annotation.Trace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

    //생성자 생성, @Autowired 자동 의존 주입
    private final ExamRepository examRepository;

    //메서드 호출 정보를 AOP를 사용해서 로그로 남길 수 있다.
    @Trace
    public void request(String itemId) {
        examRepository.save(itemId);
    }
}
