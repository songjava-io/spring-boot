package kr.songjava.web.security.userdetails;

/**
 * OAuthUser2 카카오 계정
 */
public record OAuth2KakaoAccount(
	int id, 
	String nickname, 
	String imageUrl, 
	String email, 
	String gender) {
}
