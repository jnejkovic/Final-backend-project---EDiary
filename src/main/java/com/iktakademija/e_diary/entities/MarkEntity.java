
package com.iktakademija.e_diary.entities;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktakademija.e_diary.enumerations.EMarkDescription;
import com.iktakademija.e_diary.security.Views;

@Entity
@Table(name = "mark")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@JsonView(Views.Private.class)
public class MarkEntity {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(nullable = false)
	@NotNull(message = "Mark must be provided.")
	@Min(value = 1, message = "Mark must be greater than {value}.")
	@Max(value = 5, message = "Mark must be less than {value}.")
	private Integer markValue;
	
	@Column(nullable = false)
	@NotNull(message = "Mark description must be provided.")
	@Enumerated(EnumType.STRING)
	private EMarkDescription markDescription;
	@Column(nullable = false)
	@NotNull(message = "Date must be provided.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate updatedAt;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "student")
	private StudentEntity student;
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "subject")
	private SubjectEntity subject;
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	private TeacherEntity teacher;

	@Version
	private Integer version;



	public MarkEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMarkValue() {
		return markValue;
	}

	public void setMarkValue(Integer markValue) {
		this.markValue = markValue;
	}


	public EMarkDescription getMarkDescription() {
		return markDescription;
	}

	public void setMarkDescription(EMarkDescription markDescription) {
		this.markDescription = markDescription;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	

	

}
