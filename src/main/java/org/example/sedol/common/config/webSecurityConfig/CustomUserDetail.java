package org.example.sedol.common.config.webSecurityConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.example.sedol.domain.account.domain.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class CustomUserDetail implements UserDetails {

	private Account account;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// List<String> roles = new ArrayList<>();
		// roles.add("ROLE_" + member.getRole().toString());
		//
		// return roles.stream()
		// 	.map(SimpleGrantedAuthority::new)
		// 	.collect(Collectors.toList());
		return List.of();
	}


	@Override
	public String getPassword() {
		return account.getPassWord();
	}

	@Override
	public String getUsername() {
		return account.getUserName();
	}
}
