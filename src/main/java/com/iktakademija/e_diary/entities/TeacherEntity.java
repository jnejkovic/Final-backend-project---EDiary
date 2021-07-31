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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "teacher")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class TeacherEntity extends UserEntity {
	@JsonView(Views.Admin.class)
	@Column(nullable = false)
	private String email;
	@JsonView(Views.Admin.class)
	@Column(nullable = false)
	private String telNumber;
	@JsonView(Views.Admin.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Accreditation", joinColumns = {
			@JoinColumn(name = "Teacher_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Subject_id", nullable = false, updatable = false) })
	private List<SubjectEntity> accreditation = new ArrayList<>();
	@JsonView(Views.Admin.class)
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<TeacherDepartmentSubject> teacherDepartmentSubject=new ArrayList<>();
	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<MarkEntity> marks=new ArrayList<>();
	public TeacherEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<SubjectEntity> getAccreditation() {
		return accreditation;
	}

	public void setAccreditation(List<SubjectEntity> accreditation) {
		this.accreditation = accreditation;
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

	public List<TeacherDepartmentSubject> getTeacherDepartmentSubject() {
		return teacherDepartmentSubject;
	}

	public void setTeacherDepartmentSubject(List<TeacherDepartmentSubject> teacherDepartmentSubject) {
		this.teacherDepartmentSubject = teacherDepartmentSubject;
	}

	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}
	
	

}
