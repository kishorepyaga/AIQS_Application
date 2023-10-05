package com.wipro.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.entity.UserProfile;
import com.wipro.repository.UserProfileRepository;

@Configuration
@Transactional
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserProfileRepository userRepo;
	
	@Bean
	PasswordEncoder newPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder(); //new BCryptPasswordEncoder();
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserProfile user = userRepo.findByEmailId(username).orElseThrow(() -> new UsernameNotFoundException("User With EmailId: "+username+" is Not Found"));
		String password = newPasswordEncoder().encode(user.getPassword());
		return new User(username, password, getAuthorities(user));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(UserProfile user) {
		//if we have more than one role
		//String[] userRoles = user.getRole(); //Should return them as a Array
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRole());
		return authorities;
	}

}
