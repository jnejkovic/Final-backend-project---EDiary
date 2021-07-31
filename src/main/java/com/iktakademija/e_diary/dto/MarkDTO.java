package com.iktakademija.e_diary.dto;

import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iktakademija.e_diary.enumerations.EMarkDescription;

public class MarkDTO {

	@NotNull(message = "Mark must be provided.")
	@Min(value = 1, message = "Mark must be greater than {value}.")
	@Max(value = 5, message = "Mark must be less than {value}.")
	private Integer markValue;
	@NotNull(message = "Mark description must be provided.")
	@Enumerated(EnumType.STRING)
	private EMarkDescription markDescription;
	@NotNull(message = "Student id must be provided")
	private Integer studentId;
	@NotNull(message = "Subject id must be provided")
	private Integer subjectId;
	@NotNull(message = "Teacher id must be provided")
	private Integer teacherId;

	public MarkDTO() {
		super();
		// TODO Auto-generated constructor stub
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


	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}

}
