package com.iktakademija.e_diary.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encryption {
	
	public static String getPasswordEncoded(String password) {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
	
	public static boolean validatePassword(String password, String encriptedPassword) {
		BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
		return encoder.matches(password, encriptedPassword);
	}

	public static void main(String[] args) {
		System.out.println(getPasswordEncoded("pass123"));
	}
}
