package com.iktakademija.e_diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class StudentDTO {
	@NotBlank(message="Username must be provided.")
	@Size(min=5, max=20, message="Username must be between {min} and {max} characters long.")
	private String username;
	@NotBlank(message="Password must be provided.")
	@Size(min=5, max=100, message="Password must be between {min} and {max} characters long.")
	private String password;
	@NotBlank(message="First name must be provided.")
	@Size(min=2, max=15, message="First name must be between {min} and {max} characters long.")
	private String firstName;
	@NotBlank(message="Last name must be provided.")
	@Size(min=2, max=15, message="Last name must be between {min} and {max} characters long.")
	private String lastName;
	@NotBlank(message="Jmbg must be provided.")
	@Size(min=13, max=13, message="Jmbg must be {max} characters long.")
	private String jmbg;
	public StudentDTO() {
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
	
	
}
