package com.iktakademija.e_diary.dto;

import javax.validation.constraints.Size;

public class UpdateStudentDTO {
	@Size(min = 5, max = 20, message = "Password must be between {min} and {max} characters long.")
	private String password;
	@Size(min = 2, max = 15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	@Size(min = 2, max = 15, message = "Last name must be between {min} and {max} characters long.")
	private String lastName;
	@Size(min = 13, max = 13, message = "Jmbg must be {max} characters long.")
	private String jmbg;
	private boolean isActive;

	public UpdateStudentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	

}
