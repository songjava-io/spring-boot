package kr.songjava.web.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import kr.songjava.web.domain.Board;
import kr.songjava.web.form.BoardSaveForm;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import kr.songjava.web.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class BoardAspect {
	
	private final BoardService boardService;

	@Pointcut("execution(* kr.songjava.web.controller.BoardController.edit(..))"
			+ "||execution(* kr.songjava.web.controller.BoardController.delete(..))"
			+ "||execution(* kr.songjava.web.controller.BoardController.update(..))")
	private void check() {
	}
	
	/**
	 * Null체크, 로그인사람이 작성한 글인지 체크.
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Before("check()")
	public Object checkBefore(JoinPoint point) throws Throwable {
		log.info("checkBefore 위에조건에 성립된 메소드가 실행 전에");
		// SecurityContextHolder로도 인증정보를 가져올 수 있다.
		Authentication authentication = 
			SecurityContextHolder.getContext().getAuthentication();
		SecurityUserDetails details = (SecurityUserDetails) 
				authentication.getPrincipal();
		Object[] args = point.getArgs();
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        int boardSeq = 0;
        // 호출될 대상의 메소드에서 원하는 매개변수 이름을 찾아서 값을 획득
        for (int i = 0; i < method.getParameters().length; i++) {
            String parameterName = method.getParameters()[i].getName();
            // int boardSeq로 선언된 부분
            if (parameterName.equals("boardSeq")) {
            	boardSeq = Integer.parseInt(args[i].toString());
            	break;
            }
            // BoardForm form으로 선언된 부분
            if (parameterName.equals("form")) {
            	BoardSaveForm form = (BoardSaveForm) args[i];
            	boardSeq = form.getBoardSeq();
            	break;
            }
        }
		Board board = boardService.selectBoard(boardSeq);
		log.info("board.getMemberSeq() : {}", board.getMemberSeq());
		log.info("details.getMemberSeq() : {}", details.getMemberSeq());
		Assert.notNull(board, "게시글 정보가 없습니다.");
		Assert.isTrue(board.getMemberSeq() == details.getMemberSeq(), 
				"게시글 작성자만 변경이 가능합니다.");
		return point.getTarget();
	}
	
	@After("check()")
	public Object checkAfter(JoinPoint point) throws Throwable {
		log.info("checkAfter 위에조건에 성립된 메소드가 실행 된 후에");
		return point.getTarget();
	}
	
	@AfterReturning("check()")
	public Object checkReturning(JoinPoint point) throws Throwable {
		log.info("checkReturning 위에조건에 성립된 메소드 호출 성공 실행 시");
		return point.getTarget();
	}
	
	@AfterThrowing("check()")
	public Object checkAfterThrowing(JoinPoint point) throws Throwable {
		log.info("checkAfterThrowing 위에조건에 성립된 메소드 호출 실패 예외 발생");
		return point.getTarget();
	}
	
}