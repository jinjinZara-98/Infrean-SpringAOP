package hello.aop.member;

import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import org.springframework.stereotype.Component;

//만들어준 aop적용, 클래스에 붙이는
//테스트 코드 AtAnnotationTest 에서 어드바이저 만들어 @ClassAop @MethodAop 테스트
@ClassAop
@Component
public class MemberServiceImpl implements MemberService {

    //만들어준 aop적용, 메서드에 붙이는
    @Override
    @MethodAop("test value")
    public String hello(String param) {
        return "ok";
    }

    //오버리이딩 안하고 내부에서만 가지고 있는
    public String internal(String param) {
        return "ok";
    }
}

