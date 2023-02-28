package kr.songjava.web.mapper;

import org.apache.ibatis.annotations.Param;

import kr.songjava.web.domain.Member;

public interface MemberMapper {

	int selectMemberAccountCount(String account);
	
	void insertMember(Member member);

	Member selectMemberAccount(String account);
	
	Member selectMemberOauth2(@Param("oauth2ClientName") String oauth2ClientName, 
		@Param("oauth2Id") String oauth2Id);
	
	void updateMemberLoginDate(int memberSeq);
	
}
