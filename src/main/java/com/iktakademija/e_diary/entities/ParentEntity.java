package com.iktakademija.e_diary.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "parent")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })

public class ParentEntity extends UserEntity {
	@JsonView(Views.Admin.class)
	@Column(nullable = false)
	private String email;
	@JsonView(Views.Admin.class)
	@Column(nullable = false)
	private String telNum;
	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Parents_Children", joinColumns = {
			@JoinColumn(name = "Parent_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Student_id", nullable = false, updatable = false) })

	private List<StudentEntity> children = new ArrayList<>();

	public ParentEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<StudentEntity> getChildren() {
		return children;
	}

	public void setChildren(List<StudentEntity> children) {
		this.children = children;
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
