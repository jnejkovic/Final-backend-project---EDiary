package com.iktakademija.e_diary.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class UserEntity {
	@JsonView(Views.Public.class)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "user_id")
	private Integer id;
	@JsonView(Views.Admin.class)
	@Column(unique = true, nullable = false)
	@NotBlank(message="Username must be provided.")
	@Size(min=5, max=20, message="Username must be between {min} and {max} characters long.")
	private String username;
	@Column(nullable = false)
	@JsonIgnore
	@NotBlank(message="Password must be provided.")
	@Size(min=5, max=100, message="Password must be between {min} and {max} characters long.")
	private String password;
	@JsonView(Views.Public.class)
	@Column(name="first_name",nullable = false)
	@NotBlank(message="First name must be provided.")
	@Size(min=2, max=15, message="First name must be between {min} and {max} characters long.")
	private String firstName;
	@JsonView(Views.Public.class)
	@Column(name = "last_name", nullable = false)
	@NotBlank(message="Last name must be provided.")
	@Size(min=2, max=15, message="First name must be between {min} and {max} characters long.")
	private String lastName;
	@JsonView(Views.Admin.class)
	@Column(nullable = false)
	private Boolean isActive;
	@Version
	private Integer version;
	@JsonView(Views.Admin.class)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "role")
	private RoleEntity role;

	public UserEntity() {
		super();
		this.isActive=true;
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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


	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public Boolean isActive() {
		return isActive;
	}


	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}


	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

}
