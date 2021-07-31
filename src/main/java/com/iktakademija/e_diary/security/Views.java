package com.iktakademija.e_diary.security;

public class Views {

	public static class Public {
	}

	public static class Private extends Public {
	}
	
	public static class Teacher extends Private{
		
	}

	public static class Admin extends Teacher {
	}
}
