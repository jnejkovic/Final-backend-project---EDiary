package com.iktakademija.e_diary.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.swing.text.View;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "admin")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonView(Views.Admin.class)
public class AdminEntity extends UserEntity {
	@Column(nullable = false)
	private String email;
	@Column(nullable = false)
	private String telNumber;



	public AdminEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

}
