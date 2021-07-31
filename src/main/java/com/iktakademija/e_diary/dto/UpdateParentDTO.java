package com.iktakademija.e_diary.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateParentDTO {
	@Size(min = 5, max = 20, message = "Username must be between {min} and {max} characters long.")
	private String password;
	@Size(min = 2, max = 15, message = "First name must be between {min} and {max} characters long.")
	private String firstName;
	@Size(min = 2, max = 15, message = "Last name must be between {min} and {max} characters long.")
	private String lastName;
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Email is not valid.")
	private String email;
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone number is not valid")
	private String telNum;

	public UpdateParentDTO() {
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
