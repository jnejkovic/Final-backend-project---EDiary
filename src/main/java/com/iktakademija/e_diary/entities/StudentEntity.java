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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "student")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })

public class StudentEntity extends UserEntity {
	@JsonView(Views.Admin.class)
	@Column(nullable = false, unique = true)
	@NotBlank(message = "Jmbg must be provided.")
	@Size(min = 13, max = 13, message = "Jmbg must be {max} characters long.")
	private String jmbg;
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "department")
	@JsonView(Views.Private.class)
	private DepartmentEntity department;
	@JsonView(Views.Private.class)
	@JsonIgnore
	@OneToMany(mappedBy = "student", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<MarkEntity> marks = new ArrayList<>();
	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Parents_Children", joinColumns = {
			@JoinColumn(name = "Student_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Parent_id", nullable = false, updatable = false) })
	private List<ParentEntity> parents = new ArrayList<>();

	public StudentEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}

	public DepartmentEntity getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentEntity department) {
		this.department = department;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

	public List<ParentEntity> getParents() {
		return parents;
	}

	public void setParents(List<ParentEntity> parents) {
		this.parents = parents;
	}

}
