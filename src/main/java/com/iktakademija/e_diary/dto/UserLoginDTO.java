package com.iktakademija.e_diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserLoginDTO {
	@NotBlank(message = "Username must be provided.")
	@Size(min = 5, max = 20, message = "Username must be between {min} and {max} characters long.")
	private String username;
	@NotBlank(message = "Password must be provided.")
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long.")
	private String password;

	public UserLoginDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
