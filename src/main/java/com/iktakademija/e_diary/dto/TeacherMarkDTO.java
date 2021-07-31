package com.iktakademija.e_diary.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.iktakademija.e_diary.enumerations.EMarkDescription;

public class TeacherMarkDTO {

	@NotNull(message = "Mark must be provided.")
	@Min(value = 1, message = "Mark must be greater than {value}.")
	@Max(value = 5, message = "Mark must be less than {value}.")
	private Integer markValue;
	@NotNull(message = "Mark description must be provided.")
	@Enumerated(EnumType.STRING)
	private EMarkDescription markDescription;
	public TeacherMarkDTO() {
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
	
}
