package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.ServletException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jwt Token 매니저
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenManager  {
	
	private static final String SUBJECT = "JWT_AUTH_TOKEN";
	private static final String CLAIM_USERNAME = "username";
	private static final String CLAIM_ROLES = "roles";
	private static final String CLAIM_OAUTH2_USER_HASH_KEY = "OAUTH2_USER_HASH_KEY";
	private static final int EXPIRED = (60 * 60) * 24; // JWT 토큰 만료시간
	
	private final JwtEncoder encoder; // Jwt Encoder
	private final JwtDecoder decoder;
	
	public String encodeLogin(JwtTokenClaimLogin claimLogin) throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities = claimLogin.authorities();
		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority authoritie : authorities) {
			roles.add(authoritie.getAuthority());
		}
		
		Consumer<Map<String, Object>> claims = claim -> {
			claim.put(CLAIM_USERNAME, claimLogin.username());
			claim.put(CLAIM_ROLES, roles);
		};
		
		return encode(claims);
	}
	
	public String encodeOAuth(String hashKey) throws IOException, ServletException {
		Consumer<Map<String, Object>> claims = claim -> {
			claim.put(CLAIM_OAUTH2_USER_HASH_KEY, hashKey);
		};
		return encode(claims);
	}
	
	private String encode(Consumer<Map<String, Object>> claims) throws IOException, ServletException {
		// jwt에 사용할 정보 set
		JwtClaimsSet claimsSet = JwtClaimsSet.builder()
			.subject(SUBJECT)
			.claims(claims)
			.expiresAt(Instant.now().plusSeconds(EXPIRED))
			.build();
		// jwt 생성
		Jwt jwt = encoder.encode(
			JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(), 
				claimsSet
			)
		);
		// 암호화된 토큰
		String tokenValue = jwt.getTokenValue();
		log.info("tokenValue : {}", tokenValue);
		return tokenValue;
	}
	
	public JwtTokenClaimLogin decodeLogin(Authentication authentication) {
		String tokenValue = authentication.getPrincipal().toString();
		Jwt jwt = decoder.decode(tokenValue);
		String username = jwt.getClaim(CLAIM_USERNAME);
		if (!StringUtils.hasLength(username)) {
			return null;
		}
		// 토큰에 username이 실제 DB에 존재하는지 체크와 회원 정보를 인증에 사용하기 위한.
		List<String> roles = jwt.getClaimAsStringList(CLAIM_ROLES);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return new JwtTokenClaimLogin(username, authorities);
	}

	public String decodeOAuth(Authentication authentication) {
		String tokenValue = authentication.getPrincipal().toString();
		Jwt jwt = decoder.decode(tokenValue);
		return jwt.getClaim(CLAIM_OAUTH2_USER_HASH_KEY);
	}

}
