package kr.songjava.web.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;

import kr.songjava.web.configuration.properties.FileProperties;
import kr.songjava.web.configuration.properties.FrontendProperties;
import kr.songjava.web.security.userdetails.DefaultAuthenticationSuccessHandler;
import kr.songjava.web.security.userdetails.JwtTokenAuthenticationManager;
import kr.songjava.web.security.userdetails.JwtTokenAuthenticationSuccessHandler;
import kr.songjava.web.security.userdetails.Oauth2AuthenticationSuccessHandler;
import kr.songjava.web.security.userdetails.SecurityOauth2Service;
import kr.songjava.web.security.userdetails.UsernamePasswordAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigruation {

	private static final String JWT_SECRET_KEY = "asdfcdsr432rsdcsdfsdfdsfsdfsfdfcds";
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
		JwtTokenAuthenticationSuccessHandler jwtTokenAuthenticationSuccessHandler,
		JwtTokenAuthenticationManager jwtTokenAuthenticationManager,
		Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
		SecurityOauth2Service securityOauth2Service, DefaultAuthenticationSuccessHandler defaultAuthenticationSuccessHandler,
		FileProperties fileProperties,
		CorsConfigurationSource corsConfigurationSource,
		UsernamePasswordAuthenticationFailureHandler usernamePasswordAuthenticationFailureHandler) throws Exception {
		http.authorizeRequests()
			// 해당 url 패턴은 로그인 권한없어도 접근되게
			.antMatchers(
				"/",
				"/public/**", 
				"/member/save",
				"/file/download",
				fileProperties.resourcePath()
			)
			.permitAll()
			// 나머지 요청은 로그인을 해야 접근되게
			.anyRequest().hasRole("USER").and()
		//	.oauth2Login().successHandler(oauth2AuthenticationSuccessHandler).failureHandler(usernamePasswordAuthenticationFailureHandler).
			//	.userInfoEndpoint().userService(securityOauth2Service)
			//.and().and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)		
			.and()
			.formLogin(form -> {
				form.successHandler(jwtTokenAuthenticationSuccessHandler);
				form.failureHandler(usernamePasswordAuthenticationFailureHandler);
				form.permitAll();
			})
			.csrf().disable()
			.cors().configurationSource(corsConfigurationSource)
			.and()
			// Jwt Token이 서버로 오는경우 토큰 인증에 필요하기 때문에
			.addFilterBefore(
				new BearerTokenAuthenticationFilter(jwtTokenAuthenticationManager),
					AnonymousAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource(FrontendProperties frontendProperties) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern(frontendProperties.domain());
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	/**
	 * 비밀번호 인코더 등록
	 * 등록안하면 There is no PasswordEncoder mapped for the id "null" 에러가 발생
	 * @return
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(JWT_SECRET_KEY.getBytes()));
	}
	
	@Bean
	JwtDecoder jwtDecoder() {
		SecretKeySpec secretKeySpec = new SecretKeySpec(JWT_SECRET_KEY.getBytes(), JWSAlgorithm.HS256.getName());
		return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
	}

}