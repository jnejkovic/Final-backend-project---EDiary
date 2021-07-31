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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "subject")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class SubjectEntity {
	@JsonView(Views.Private.class)
	@Id
	@GeneratedValue
	private Integer id;
	@JsonView(Views.Private.class)
	@Column(nullable = false)
	@NotNull(message = "Subject name must be provided")
	@Size(min=3, max=20, message="Subject name must be between {min} and {max} characters long.")
	private String subjectName;
	@JsonView(Views.Private.class)
	@Column(nullable = false)
	@NotNull(message = "Subject description must be provided")
	@Size(min=3, max=50, message="Subject description must be between {min} and {max} characters long.")
	private String subjectDescription;
	@JsonView(Views.Private.class)
	@Column(nullable = false)
	@NotNull(message="Type of subject must be provided")
	private Boolean isMandatory;
	@JsonView(Views.Private.class)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "classEntity")
	private ClassEntity classEntity;
	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@OneToMany(mappedBy = "subject", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<MarkEntity> marks = new ArrayList<>();
	@JsonView(Views.Admin.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Accreditation", joinColumns = {
			@JoinColumn(name = "Subject_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Teacher_id", nullable = false, updatable = false) })

	private List<TeacherEntity> teachers = new ArrayList<>();
	@JsonView(Views.Admin.class)
	@JsonIgnore
	@OneToMany(mappedBy = "subject", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<TeacherDepartmentSubject> teacherDepartmentSubject = new ArrayList<>();
	@Version
	private Integer version;

	public SubjectEntity() {
		super();
		this.isMandatory = true;
	}


	public List<MarkEntity> getMarks() {
		return marks;
	}

	public void setMarks(List<MarkEntity> marks) {
		this.marks = marks;
	}

	public List<TeacherEntity> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<TeacherEntity> teachers) {
		this.teachers = teachers;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectDescription() {
		return subjectDescription;
	}

	public void setSubjectDescription(String subjectDescription) {
		this.subjectDescription = subjectDescription;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public ClassEntity getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}

	public List<TeacherDepartmentSubject> getTeacherDepartmentSubject() {
		return teacherDepartmentSubject;
	}

	public void setTeacherDepartmentSubject(List<TeacherDepartmentSubject> teacherDepartmentSubject) {
		this.teacherDepartmentSubject = teacherDepartmentSubject;
	}

}
