package com.iktakademija.e_diary.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "department")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonView(Views.Private.class)
public class DepartmentEntity {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(nullable = false)
	private String name;
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "classEntity")
	private ClassEntity classEntity;
	@JsonIgnore
	@OneToMany(mappedBy = "department", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<StudentEntity> students = new ArrayList<>();
	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "classelder")
	private TeacherEntity classElder;
	@JsonIgnore
	@OneToMany(mappedBy = "department", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<TeacherDepartmentSubject> teacherDepartmentSubject = new ArrayList<>();
	@Version
	private Integer version;

	public DepartmentEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassEntity getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}

	public TeacherEntity getClassElder() {
		return classElder;
	}

	public void setClassElder(TeacherEntity classElder) {
		this.classElder = classElder;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<TeacherDepartmentSubject> getTeacherDepartmentSubject() {
		return teacherDepartmentSubject;
	}

	public void setTeacherDepartmentSubject(List<TeacherDepartmentSubject> teacherDepartmentSubject) {
		this.teacherDepartmentSubject = teacherDepartmentSubject;
	}

}
