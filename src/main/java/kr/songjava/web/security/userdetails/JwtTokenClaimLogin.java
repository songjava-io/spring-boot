package kr.songjava.web.security.userdetails;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public record JwtTokenClaimLogin(String username, Collection<? extends GrantedAuthority> authorities) {

}
