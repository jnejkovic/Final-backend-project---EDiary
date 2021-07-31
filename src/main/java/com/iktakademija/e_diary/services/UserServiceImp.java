package com.iktakademija.e_diary.services;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

	@Override
	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = null;
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			currentUsername = authentication.getName();
		}
		return currentUsername;

	}

}
