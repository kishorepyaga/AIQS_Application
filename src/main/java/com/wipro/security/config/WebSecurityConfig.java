package com.wipro.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class WebSecurityConfig {
	
	@Autowired
	private UserDetailsService customUserDetailsService;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		MvcRequestMatcher.Builder mvcRequestMatcher = new MvcRequestMatcher.Builder(introspector);
		http.csrf(c -> c.disable());//(c -> c.ignoringRequestMatchers(mvcRequestMatcher.pattern("/"), PathRequest.toH2Console()));
		http.headers(head -> head
				.frameOptions(frame -> frame
						.sameOrigin()));
		//.frameOptions().sameOrigin();
//		.and()
			http.authorizeHttpRequests(auth -> auth
					.requestMatchers(mvcRequestMatcher.pattern("/")).permitAll()
					.requestMatchers(mvcRequestMatcher.pattern("/public/**")).permitAll()
					.requestMatchers(mvcRequestMatcher.pattern("/welcome")).permitAll()
					.requestMatchers(mvcRequestMatcher.pattern("/admin/**")).hasRole("ADMIN")
					.requestMatchers(PathRequest.toH2Console()).permitAll()
					.requestMatchers(mvcRequestMatcher.pattern("/**")).hasAnyRole("ADMIN", "USER")
					)

//			.requestMatchers("/resources/**", "/webjars/**", "/assets/**").permitAll()
//			.requestMatchers(mvcRequestMatcher.pattern("/")).permitAll()
//			.requestMatchers(mvcRequestMatcher.pattern("/public/**")).permitAll()
//			.requestMatchers(mvcRequestMatcher.pattern("/admin/**")).hasRole("ADMIN")
//			
//			.requestMatchers(PathRequest.toH2Console()).permitAll()
//			.anyRequest().authenticated()
//			.and()
				.formLogin(log -> log
					.loginPage("/login")
					.defaultSuccessUrl("/home")
					.failureUrl("/login?error")
					.permitAll())
//				.loginPage("/login")
//				.defaultSuccessUrl("/home")
//				.failureUrl("/login?error")
//				.permitAll()
//				.and()
					.logout(log -> log
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/login?logout")
						.deleteCookies("my-remember-me-cookie")
						.permitAll())
//					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//					.logoutSuccessUrl("/login?logout")
//					.deleteCookies("my-remember-me-cookie")
//					.permitAll()
//					.and()
				.rememberMe(rem -> rem
						.rememberMeCookieName("my-remember-me-cookie")
						.tokenRepository(persistentTokenRepository())
						.tokenValiditySeconds(24 * 60 * 60))
//				.key("my-secure-key")
//				.rememberMeCookieName("my-remember-me-cookie")
//				.tokenRepository(persistentTokenRepository())
//				.tokenValiditySeconds(24 * 60 * 60)
//				.and()
			.exceptionHandling(Customizer.withDefaults());
//					e -> e.accessDeniedPage("/welcome?error"));
		http.authenticationProvider(daoAuthenticationProvider());
		return http.build();
	}
	
	//To save User Tokens in the jdbcTokenRepository
	PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
		tokenRepo.setDataSource(dataSource);
		return tokenRepo;
	}
	
	//Provides Authentication of the User
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
}
