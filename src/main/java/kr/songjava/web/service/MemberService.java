package kr.songjava.web.service;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.songjava.web.domain.Member;
import kr.songjava.web.mapper.MemberMapper;
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
	public void save(Member member) {
		String password = member.getPassword();
		String encodePassword = passwordEncoder.encode(password);

		log.info("password : {}", password);
		log.info("encodePassword : {}", encodePassword);
		
		member.setPassword(encodePassword);
		
		memberMapper.insertMember(member);
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
	 * Oauth2 회원 조회
	 * @param oauth2Provider
	 * @param oauth2Id
	 * @return
	 */
	public Member selectMemberOAuth2(String oauth2Provider, String oauth2Id) {
		return memberMapper.selectMemberOAuth2(oauth2Provider, oauth2Id);
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
