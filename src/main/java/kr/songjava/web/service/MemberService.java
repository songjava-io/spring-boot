package kr.songjava.web.service;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.songjava.web.domain.Member;
import kr.songjava.web.domain.Message;
import kr.songjava.web.domain.MessageState;
import kr.songjava.web.domain.MessageType;
import kr.songjava.web.mapper.MemberMapper;
import kr.songjava.web.mapper.MessageMapper;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 서비스
 * @author youtube
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

	private final MemberMapper memberMapper;
	private final MessageMapper messageMapper;
	
	private final PasswordEncoder passwordEncoder;
	
	/**
	 * 회원 계정 사용여부
	 * @param account
	 * @return
	 */
	public boolean isUseAccount(String account) {
		int count = memberMapper.selectMemberAccountCount(account);
		return count > 0;
	}
	
	/**
	 * 회원 등록
	 * @param member
	 */
	@Transactional
	public void save(Member member) {
		String password = member.getPassword();
		String encodePassword = passwordEncoder.encode(password);

		log.info("password : {}", password);
		log.info("encodePassword : {}", encodePassword);
		
		member.setPassword(encodePassword);
		
		memberMapper.insertMember(member);
		
		// 회원가입 성공 후 메일 전송을 위한 DB에 추가
		Message message = new Message();
		message.setSubject("회원가입 안내 메일");
		message.setContents("회원가입을 축하드립니다.");
		message.setState(MessageState.R);
		message.setMsgType(MessageType.MAIL);
		message.setSendEmail("master@fastcampus.co.kr");
		message.setReceiveEmail(member.getAccount());
		messageMapper.insertMessage(message);
	}
	
	/**
	 * 회원 로그인 일자 업데이트
	 * @param memberSeq
	 */
	public void updateLoginDate(int memberSeq) {
		memberMapper.updateMemberLoginDate(memberSeq);
	}

	public Member selectMemberAccount(String username) {
		return memberMapper.selectMemberAccount(username);
	}
	
	/**
	 * OAuth2 인증 방식의 로그인 대상 회원 조회
	 * @param oauth2ClientName
	 * @param oauth2Id
	 * @return
	 */
	public Member selectMemberOauth2(String oauth2ClientName, String oauth2Id) {
		return memberMapper.selectMemberOauth2(oauth2ClientName, oauth2Id);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername : {}", username);
		Member member = memberMapper.selectMemberAccount(username);
		if (member == null) {
			throw new UsernameNotFoundException("회원이 존재하지 않습니다.");
		}
		log.info("member : {}", member);
		return SecurityUserDetails.builder()
			.memberSeq(member.getMemberSeq())
			.nickname(member.getNickname())
			.username(username)
			.password(member.getPassword())
			.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
			.build();
	}
	
}
