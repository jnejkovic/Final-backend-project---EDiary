package com.iktakademija.e_diary.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.context.support.BeanDefinitionDsl.Role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.enumerations.ERole;
import com.iktakademija.e_diary.security.Views;


@Entity
@Table(name = "role")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonView(Views.Admin.class)
public class RoleEntity {
	@Id
	@GeneratedValue
	@Column(name = "role_id")
	private Integer id;
	@Column(name = "role_name")
	@Enumerated(EnumType.STRING)
	private ERole roleName;
	@Version
	private Integer version;
	@JsonIgnore
	@OneToMany(mappedBy = "role", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<UserEntity> users = new ArrayList<>();

	public RoleEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RoleEntity(ERole name) {
		super();
		this.roleName = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ERole getRoleName() {
		return roleName;
	}

	public void setRoleName(ERole roleName) {
		this.roleName = roleName;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

}
