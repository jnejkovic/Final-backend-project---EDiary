package com.iktakademija.e_diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordDTO {

	@NotBlank(message = "Password must be provided.")
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long.")
	private String oldPassword;
	@NotBlank(message = "Password must be provided.")
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long.")
	private String newPassword;

	public ChangePasswordDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}



}
