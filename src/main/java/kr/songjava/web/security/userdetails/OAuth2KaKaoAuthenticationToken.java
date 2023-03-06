package kr.songjava.web.security.userdetails;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

public class OAuth2KaKaoAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final OAuth2KakaoAccount principal;

	public OAuth2KaKaoAuthenticationToken(OAuth2KakaoAccount principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		Assert.notNull(principal, "principal cannot be null");
		this.principal = principal;
		this.setAuthenticated(true);
	}

	@Override
	public OAuth2KakaoAccount getPrincipal() {
		return this.principal;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

}
