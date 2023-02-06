package kr.songjava.web.mapper;

import kr.songjava.web.domain.Member;

public interface MemberMapper {

	int selectMemberAccountCount(String account);
	
	void insertMember(Member member);

	Member selectMemberAccount(String account);
	
	void updateMemberLoginDate(int memberSeq);
	
}
