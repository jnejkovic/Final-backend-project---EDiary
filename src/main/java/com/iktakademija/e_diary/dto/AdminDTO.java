package com.iktakademija.e_diary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AdminDTO {
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
	@NotBlank(message="Email must be provided.")
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
	message="Email is not valid.")
	private String email;
	@NotBlank(message="Phone number must be provided.")
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number is not valid")
	private String telNum;

	public AdminDTO(String username, String password, String firstName, String lastName, String email,
			String telNum) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.telNum = telNum;
	}

	public AdminDTO() {
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

}
