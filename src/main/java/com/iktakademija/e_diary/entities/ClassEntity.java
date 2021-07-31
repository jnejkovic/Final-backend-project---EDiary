package com.iktakademija.e_diary.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "class")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonView(Views.Teacher.class)
public class ClassEntity {
	@Id
	@GeneratedValue
	private Integer id;
	@Column(nullable = false)
	@Min(value = 1, message = "Class must be greather than or equal to 1")
	@Max(value = 8, message = "Class must be less or equal to 8")
	private Integer classNum;
	@JsonIgnore
	@OneToMany(mappedBy = "classEntity", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<DepartmentEntity> departments = new ArrayList<>();
	@JsonIgnore
	@OneToMany(mappedBy = "classEntity", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<SubjectEntity> subjects = new ArrayList<>();
	@Version
	private Integer version;

	public ClassEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getClassNum() {
		return classNum;
	}

	public void setClassNum(Integer classNum) {
		this.classNum = classNum;
	}

	public List<DepartmentEntity> getDepartments() {
		return departments;
	}

	public void setDepartments(List<DepartmentEntity> departments) {
		this.departments = departments;
	}

	public List<SubjectEntity> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
